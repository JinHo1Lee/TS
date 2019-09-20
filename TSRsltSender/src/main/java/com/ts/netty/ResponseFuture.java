package com.ts.netty;


import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public class ResponseFuture implements Future<byte[]> {

	private volatile State state = State.WAITING;
	ArrayBlockingQueue<byte[]> blockingResponse = new ArrayBlockingQueue<byte[]>(1);

	private enum State {
		WAITING,
		DONE
	}

	public boolean isCancelled() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Should return true when:
	 *  - Future completed normally
	 *  - Future generated an exception
	 *  - Future got cancelled
	 * 
	 */
	public boolean isDone() {
		return state == State.DONE;
	}

	/**
	 * Retrieves the response, blocking if necessary.
	 * 
	 * As ArrayBlockingQueue is the structure being used to hold the response,
	 * this adaptation puts the response back in the queue.. so that future
	 * calls to get() will return the same value.
	 * 
	 */
	public byte[] get() throws InterruptedException, ExecutionException {
		byte[] aux = blockingResponse.take();
		blockingResponse.put(aux);
		return aux;
	}

	public byte[] get(long timeout, TimeUnit unit) throws InterruptedException,
			ExecutionException, TimeoutException {
		final byte[] responseAfterWait = blockingResponse.poll(timeout, unit);
		if (responseAfterWait == null) {
			throw new TimeoutException();
		}
		return responseAfterWait;
	}

	public boolean isSuccess() {
		return state == State.DONE;
	}

	public boolean isCancellable() {
		return false;
	}

	/**
	 * This method is meant to be used by the service that provides the
	 * asynchronous answer.
	 * 
	 * This method will be called only once.
	 * 
	 * @param msg
	 */
	public void set(byte[] msg) {
		if (state == State.DONE) {
			return;
		}
		
		try {
			blockingResponse.put(msg);
			state = State.DONE;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}		
	}

	public Future<byte[]> addListener(GenericFutureListener<? extends Future<? super byte[]>> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Future<byte[]> addListeners(GenericFutureListener<? extends Future<? super byte[]>>... arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Future<byte[]> await() throws InterruptedException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean await(long arg0) throws InterruptedException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean await(long arg0, TimeUnit arg1) throws InterruptedException {
		// TODO Auto-generated method stub
		return false;
	}

	public Future<byte[]> awaitUninterruptibly() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean awaitUninterruptibly(long arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean awaitUninterruptibly(long arg0, TimeUnit arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean cancel(boolean arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public Throwable cause() {
		// TODO Auto-generated method stub
		return null;
	}

	public byte[] getNow() {
		// TODO Auto-generated method stub
		return null;
	}

	public Future<byte[]> removeListener(GenericFutureListener<? extends Future<? super byte[]>> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Future<byte[]> removeListeners(GenericFutureListener<? extends Future<? super byte[]>>... arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Future<byte[]> sync() throws InterruptedException {
		// TODO Auto-generated method stub
		return null;
	}

	public Future<byte[]> syncUninterruptibly() {
		// TODO Auto-generated method stub
		return null;
	}

}