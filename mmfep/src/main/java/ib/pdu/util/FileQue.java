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


public abstract class FileQue{
	String queName;
	int ratio;
	int cnt;
	
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
	public abstract boolean open() throws IOException;
	public abstract boolean enque(byte[] input) throws IOException;
	public abstract byte[] deque()  throws IOException;
	public abstract void close() throws IOException;
}

