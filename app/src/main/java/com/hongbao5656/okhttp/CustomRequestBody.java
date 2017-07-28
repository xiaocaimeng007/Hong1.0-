package com.hongbao5656.okhttp;

import java.io.IOException;
import okio.BufferedSink;
import okio.Okio;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;

/**
 * 暂时不用
 *
 * @author huajun
 *
 */
public class CustomRequestBody extends RequestBody {

	protected RequestBody delegate;
	protected PrograssListner listener;

	protected CountingSink countingSink;

	public CustomRequestBody(RequestBody delegate, PrograssListner listener) {
		this.delegate = delegate;
		this.listener = listener;
	}

	@Override
	public MediaType contentType() {
		return delegate.contentType();
	}

	@Override
	public long contentLength() throws IOException {
		return delegate.contentLength();
	}

	@Override
	public void writeTo(BufferedSink sink) throws IOException {
		BufferedSink bufferedSink;

		countingSink = new CountingSink(sink, contentLength(), listener);
		bufferedSink = Okio.buffer(countingSink);
		delegate.writeTo(bufferedSink);
		bufferedSink.flush();
	}

}
