package ib.pdu.util;

import ib.pdu.mfep.PDUConst;
import ib.pdu.util.ByteUtil;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.locks.Lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mysql.cj.x.protobuf.MysqlxDatatypes.Array;


public class NIOFileQue extends FileQue{
	private static final Logger LOGGER = LoggerFactory.getLogger(NIOFileQue.class);
	private String queName;
	private FileChannel fileChannel = null;
	private static final int FILE_HEADER_SIZE = 21;
	private static final String head = "INFOBANK\n";
	
	private int fileHeader;
	private int fileTail;
	private int fileSize;
	private int ratio;
	private int cnt;

	private FileLock fileLock;
	
	public NIOFileQue(String queName) {
		this.queName = queName;
		
		fileHeader = FILE_HEADER_SIZE;
		fileSize = 0;
		fileTail = FILE_HEADER_SIZE;
	}
	
	public boolean open() throws IOException{
		Path path = Paths.get(queName);
		if (!Files.exists(path.toAbsolutePath())){
			fileChannel = FileChannel.open(path, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.READ);
			initHeadTail();
		}else{
			fileChannel = FileChannel.open(path, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.READ);
		}
		
		while(true){
			try{
				fileLock = fileChannel.tryLock();
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
	
	public boolean enque(byte[] input) throws IOException{

		while(true){
			try{
				fileLock = fileChannel.tryLock();
				if(fileLock!=null){
					break;
				}
			}catch(OverlappingFileLockException e){
				continue;
			}
			
		}
		synchronized (fileChannel) {
			loadHeadTail();
			fileChannel.position(fileTail);
			ByteBuffer buffer = ByteBuffer.allocate(input.length + 4);
			buffer.putInt(input.length);
			buffer.put(input);
			buffer.limit(buffer.position());
			buffer.flip();
			fileChannel.write(buffer);
			
			
			fileTail += 4 + input.length;
			fileSize++;
			
			storeHeadTail();
		}
		
		if(fileLock!=null){
			fileLock.release();
		}

		return true;
	}
	public byte[] deque()  throws IOException {
		byte[] buffer = null;
		while(true){
			try{
				fileLock = fileChannel.tryLock();
				if(fileLock!=null){
					break;
				}
			}catch(OverlappingFileLockException e){
				continue;
			}
			
		}
		int bufSize=0;
		int itmeCnt=0;
		synchronized (fileChannel) {
			loadHeadTail();
			
			if(fileHeader == fileTail){
				if(fileLock!=null)
					fileLock.release();
				return null;
			}
			
			
			fileChannel.position(fileHeader);
			ByteBuffer tmpBuf = ByteBuffer.allocate(4);
			fileChannel.read(tmpBuf);
			bufSize = ByteUtil.getint(ByteUtil.ByteBufferToByte(tmpBuf));
			ByteBuffer byteBuffer = ByteBuffer.allocate(bufSize);
			if(fileChannel.read(byteBuffer)>0){
				buffer = new byte[bufSize];
				buffer = ByteUtil.ByteBufferToByte(byteBuffer);
				itmeCnt = fileSize;
				fileSize --;
				fileHeader += bufSize+PDUConst.SIZE_INT;
			}
			byteBuffer.flip();

			if (fileHeader == fileTail){
				initHeadTail();
			}else{
				storeHeadTail();
			}
		}
		if(fileLock!=null)
			fileLock.release();

		return buffer;
	} 
	
	private void initHeadTail() throws IOException{
		if (fileChannel!=null)
			fileChannel.truncate(fileSize);
		
		fileHeader = FILE_HEADER_SIZE;
		fileSize = 0;
		fileTail = FILE_HEADER_SIZE;
		
		storeHeadTail();
	}
	
	private void loadHeadTail() throws IOException{
		int pos = 0;
		fileChannel.position(0);
		ByteBuffer byteBuffer = ByteBuffer.allocate(21);
		fileChannel.read(byteBuffer);
		
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
	private void storeHeadTail() throws IOException{
		fileChannel.position(0);
		ByteBuffer buffer = ByteBuffer.allocate(21);
		
		buffer.put(head.getBytes());
		buffer.putInt(fileHeader);
		buffer.putInt(fileTail);
		buffer.putInt(fileSize);
		buffer.flip();

		fileChannel.write(buffer);
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
		fileChannel.close();
	}
}

