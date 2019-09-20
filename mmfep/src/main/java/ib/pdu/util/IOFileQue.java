package ib.pdu.util;

import ib.pdu.mfep.PDUConst;
import ib.pdu.mfep.PDUGwMt;
import ib.pdu.util.ByteUtil;

import static org.hamcrest.CoreMatchers.allOf;

import java.awt.RadialGradientPaint;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.concurrent.locks.Lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class IOFileQue extends FileQue{
	private static final Logger LOGGER = LoggerFactory.getLogger(FileQue.class);
	private String queName;
	private static final int FILE_HEADER_SIZE = 21;
	private static final String head = "INFOBANK\n";

	private int fileHeader;
	private int fileTail;
	private int fileSize;
	private int ratio;
	private int cnt;
	
	private FileLock fileLock;

	private RandomAccessFile randomAccessFile;
	
	public IOFileQue(String queName) {
		this.queName = queName;
		
		fileHeader = FILE_HEADER_SIZE;
		fileSize = 0;
		fileTail = FILE_HEADER_SIZE;
	}
	
	public boolean open() throws IOException{
		
		Path path = Paths.get(queName);
		if (!Files.exists(path.toAbsolutePath())){
			randomAccessFile = new RandomAccessFile(path.toAbsolutePath().toString(), "rw");
			initHeadTail();
		}else{
			randomAccessFile = new RandomAccessFile(path.toAbsolutePath().toString(), "rw");
		}
		
		while(true){
			try{
				fileLock = randomAccessFile.getChannel().tryLock();
				if(fileLock!=null){
					break;
				}
			}catch(OverlappingFileLockException e){
				continue;
			}
			
		}
		
		loadHeadTail();
		
		if(fileLock!=null)
			fileLock.release();
		
		return true;
	}
	
	public synchronized boolean enque(byte[] input) throws IOException{
		if (!Files.exists(Paths.get(queName).toAbsolutePath())){
			close();
			open();
		}
		
		synchronized (randomAccessFile) {
			while(true){
				try{
					fileLock = randomAccessFile.getChannel().tryLock();
					if(fileLock!=null){
						break;
					}
				}catch(OverlappingFileLockException e){
					/*
					try {
						
						Thread.sleep(10);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						
					}
					*/
					continue;
				}
			}

			loadHeadTail();
			randomAccessFile.seek(fileTail);
			ByteBuffer buffer = ByteBuffer.allocate(input.length + 4);
			buffer.putInt(input.length);
			buffer.put(input);
			buffer.flip();
			
			randomAccessFile.write(buffer.array());
			randomAccessFile.getFD().sync();
			
			fileTail += 4 + input.length;
			fileSize++;
			
			storeHeadTail();
			
			if(fileLock!=null)
				fileLock.release();
		}
		
		return true;
	}
	public synchronized byte[] deque()  throws IOException {
		int bufSize=0;
		int itemCnt=0;
		ByteBuffer byteBuffer = null;
		byte[] buffer = null;
		synchronized (randomAccessFile) {
			while(true){
				try{
					fileLock = randomAccessFile.getChannel().tryLock();
					if(fileLock!=null){
						break;
					}
				}catch(OverlappingFileLockException e){
					/*try {
						Thread.sleep(500);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						
					}*/
					continue;
				}
				
			}

			loadHeadTail();
			if(fileHeader == fileTail){
				if(fileLock!=null)
					fileLock.release();
				return null;
			}
			
			
			
			randomAccessFile.seek(fileHeader);
			bufSize = randomAccessFile.readInt();
			byteBuffer = ByteBuffer.allocate(bufSize);
			if(randomAccessFile.read(byteBuffer.array(), 0, bufSize)>0){
				byteBuffer.position(bufSize);
				byteBuffer.limit(byteBuffer.position());
				//System.arraycopy(ByteUtil.ByteBufferToByte(byteBuffer), 4, buffer, 0, bufSize);
				itemCnt = fileSize;
				fileSize --;
				fileHeader += bufSize+PDUConst.SIZE_INT;
			}

			if (fileHeader == fileTail){
				initHeadTail();
			}else{
				storeHeadTail();
			}
			
			if(fileLock!=null){
				fileLock.release();
			}
		}
		
		return ByteUtil.ByteBufferToByte(byteBuffer); 
	} 
	
	protected void initHeadTail() throws IOException{
		if (randomAccessFile != null){
			randomAccessFile.seek(0);
			randomAccessFile.getChannel().truncate(fileSize);
		}
		fileHeader = FILE_HEADER_SIZE;
		fileSize = 0;
		fileTail = FILE_HEADER_SIZE;
		
		storeHeadTail();
	}
	
	protected void loadHeadTail() throws IOException{
		int pos = 0;
		randomAccessFile.seek(0);
		ByteBuffer byteBuffer = ByteBuffer.allocate(21);
		randomAccessFile.read(byteBuffer.array());
		
		String tmp = new String (ByteUtil.getbytes(byteBuffer.array(), pos, head.length()));
		pos += head.getBytes().length;
		if (tmp.equals(head)){
			fileHeader= ByteUtil.getint(byteBuffer.array(), pos);
			pos += 4;
			fileTail = ByteUtil.getint(byteBuffer.array(), pos);
			pos += 4;
			fileSize = ByteUtil.getint(byteBuffer.array(), pos);
			pos += 4;
			byteBuffer.flip();
		}else{
		
		}
	}
	protected void storeHeadTail() throws IOException{
		randomAccessFile.seek(0);
		ByteBuffer buffer = ByteBuffer.allocate(21);
		
		buffer.put(head.getBytes());
		buffer.putInt(fileHeader);
		buffer.putInt(fileTail);
		buffer.putInt(fileSize);
		buffer.flip();

		randomAccessFile.write(buffer.array());
		randomAccessFile.getFD().sync();
	}
	
	public String getQueName(){
		Path path = Paths.get(queName);
		return path.toString();
	}
	public int getRatio() {
		return ratio;
	}
	public void setRatio(int ratio) {
		this.ratio = ratio;
	}
	public int getCnt() {
		return cnt;
	}
	public void setCnt(int cnt) {
		this.cnt = cnt;
	}
	
	public void close() throws IOException{
		randomAccessFile.close();
	}
}

