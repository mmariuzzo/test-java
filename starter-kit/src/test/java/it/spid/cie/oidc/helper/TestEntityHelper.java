package it.spid.cie.oidc.helper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.spid.cie.oidc.config.OIDCConstants;
import it.spid.cie.oidc.config.RelyingPartyOptions;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

public class TestEntityHelper {

	private static MockWebServer mockServer;

	@Before
	public void setUp() throws IOException {
		mockServer = new MockWebServer();
		mockServer.start();
	}

	@After
	public void tearDown() throws IOException {
		mockServer.shutdown();
	}

	@SuppressWarnings("static-access")
	@Test
	public void testClass1a() {
		RelyingPartyOptions options = new RelyingPartyOptions();

		EntityHelper helper = new EntityHelper(options);

		assertNotNull(helper);

		String url =
			"http://localhost:" + mockServer.getPort() + "/" +
			OIDCConstants.OIDC_FEDERATION_WELLKNOWN_URL;

		boolean catched = false;
		String fakeResponse = "fake-response";
		String res = "";

		try {
			mockServer.enqueue(
				new MockResponse().setBody(fakeResponse));

			res = helper.getEntityConfiguration(url);
		}
		catch (Exception e) {
			catched = true;
		}

		assertFalse(catched);
		assertEquals(fakeResponse, res);
	}

	@SuppressWarnings("static-access")
	@Test
	public void testClass1b() {
		RelyingPartyOptions options = new RelyingPartyOptions();

		EntityHelper helper = new EntityHelper(options);

		assertNotNull(helper);

		String url =
			"http://localhost:" + mockServer.getPort() + "/" +
			OIDCConstants.OIDC_FEDERATION_WELLKNOWN_URL;

		boolean catched = false;
		String fakeResponse = "fake-response";
		String res = "";

		try {
			mockServer.enqueue(
				new MockResponse().setBody(fakeResponse).setResponseCode(403));

			res = helper.getEntityConfiguration(url);
		}
		catch (Exception e) {
			catched = true;
		}

		assertTrue(catched);
	}

	@SuppressWarnings("static-access")
	@Test
	public void testClass2a() {
		RelyingPartyOptions options = new RelyingPartyOptions();

		EntityHelper helper = new EntityHelper(options);

		assertNotNull(helper);

		String url =
			"http://localhost:" + mockServer.getPort() + "/test";

		boolean catched = false;
		String fakeResponse = "fake-response";
		String res = "";

		try {
			mockServer.enqueue(
				new MockResponse().setBody(fakeResponse));

			res = helper.getEntityStatement(url);
		}
		catch (Exception e) {
			catched = true;
		}

		assertFalse(catched);
		assertEquals(fakeResponse, res);
	}

	@SuppressWarnings("static-access")
	@Test
	public void testClass2b() {
		RelyingPartyOptions options = new RelyingPartyOptions();

		EntityHelper helper = new EntityHelper(options);

		assertNotNull(helper);

		String url =
			"http://localhost:" + mockServer.getPort() + "/test";

		boolean catched = false;
		String fakeResponse = "fake-response";
		String res = "";

		try {
			mockServer.enqueue(
				new MockResponse().setBody(fakeResponse).setResponseCode(403));

			res = helper.getEntityStatement(url);
		}
		catch (Exception e) {
			catched = true;
		}

		assertTrue(catched);
	}

}
