package it.spid.cie.oidc.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;

import org.json.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;

import it.spid.cie.oidc.config.OIDCConstants;
import it.spid.cie.oidc.helper.JWTHelper;
import it.spid.cie.oidc.test.util.RPTestUtils;
import it.spid.cie.oidc.util.JSONUtil;

public class TestTrustMark {

	private static WireMockServer wireMockServer;

	@BeforeClass
	public static void setUp() throws IOException {
		wireMockServer = new WireMockServer(18000);

		wireMockServer.start();

		System.out.println("mock=" + wireMockServer.baseUrl());
	}

	@AfterClass
	public static void tearDown() throws IOException {
		wireMockServer.stop();
	}

	@Test
	public void testTrustMarkClass() {
		TrustMark tm = null;

		boolean catched = false;

		try {
			JWTHelper jwtHelper = new JWTHelper(RPTestUtils.getOptions());

			JWKSet jwkSet = createJWKSet();

			JSONObject jwks = new JSONObject(jwkSet.toJSONObject(false));
			JSONObject payload = new JSONObject()
				.put("id", "id")
				.put("iss", "iss")
				.put("sub", "sub");

			String jwt = RPTestUtils.createJWS(payload, jwks);

			tm = new TrustMark(jwt, jwtHelper);

			tm.toJSON();
			tm.toString();
		}
		catch (Exception e) {
			catched = true;
		}

		assertFalse(catched);
		assertEquals("id", tm.getId());
		assertEquals("iss", tm.getIssuer());
		assertFalse(tm.isValid());
	}

	@Test
	public void test_validateByIssuer1() {
		boolean catched = false;
		boolean res = false;

		try {
			// TrustAnchor Entity Configuration

			wireMockServer.stubFor(
				WireMock.get(
					"/" + OIDCConstants.OIDC_FEDERATION_WELLKNOWN_URL
				).willReturn(
					WireMock.ok(RPTestUtils.mockedTrustAnchorEntityConfiguration())
				));

			JWTHelper jwtHelper = new JWTHelper(RPTestUtils.getOptions());

			JSONObject jwks = RPTestUtils.mockedTrustAnchorPrivateJWKS();
			JSONObject payload = new JSONObject()
				.put("id", "id")
				.put("iss", RPTestUtils.TRUST_ANCHOR)
				.put("sub", RPTestUtils.RELYING_PARTY);

			String jwt = RPTestUtils.createJWS(payload, jwks);

			TrustMark tm = new TrustMark(jwt, jwtHelper);

			res = tm.validateByIssuer();
		}
		catch (Exception e) {
			catched = true;
		}

		assertFalse(catched);
		assertTrue(res);
	}

	@Test
	public void test_validateByIssuer2() {
		boolean catched = false;
		boolean res = false;

		try {
			// TrustAnchor Entity Configuration

			wireMockServer.stubFor(
				WireMock.get(
					"/" + OIDCConstants.OIDC_FEDERATION_WELLKNOWN_URL
				).willReturn(
					WireMock.ok(RPTestUtils.mockedTrustAnchorEntityConfiguration())
				));

			JWTHelper jwtHelper = new JWTHelper(RPTestUtils.getOptions());

			JWKSet jwkSet = createJWKSet();

			JSONObject jwks = new JSONObject(jwkSet.toJSONObject(false));
			JSONObject payload = new JSONObject()
				.put("id", "id")
				.put("iss", RPTestUtils.TRUST_ANCHOR)
				.put("sub", RPTestUtils.RELYING_PARTY);

			String jwt = RPTestUtils.createJWS(payload, jwks);

			TrustMark tm = new TrustMark(jwt, jwtHelper);

			res = tm.validateByIssuer();
		}
		catch (Exception e) {
			catched = true;
		}

		assertTrue(catched);
		assertFalse(res);
	}

	@Test
	public void test_validateByIssuer3() {
		boolean catched = false;
		boolean res = false;

		try {
			// TrustAnchor Entity Configuration

			wireMockServer.stubFor(
				WireMock.get(
					"/" + OIDCConstants.OIDC_FEDERATION_WELLKNOWN_URL
				).willReturn(
					WireMock.ok(mockedTrustAnchorEntityConfigurationBad())
				));

			JWTHelper jwtHelper = new JWTHelper(RPTestUtils.getOptions());

			JWKSet jwkSet = createJWKSet();

			JSONObject jwks = new JSONObject(jwkSet.toJSONObject(false));
			JSONObject payload = new JSONObject()
				.put("id", "id")
				.put("iss", RPTestUtils.TRUST_ANCHOR)
				.put("sub", RPTestUtils.RELYING_PARTY);

			String jwt = RPTestUtils.createJWS(payload, jwks);

			TrustMark tm = new TrustMark(jwt, jwtHelper);

			res = tm.validateByIssuer();
		}
		catch (Exception e) {
			catched = true;
		}

		assertFalse(catched);
		assertFalse(res);
	}

	private static JWKSet createJWKSet() throws Exception {
		RSAKey rsaKey1 = JWTHelper.createRSAKey(JWSAlgorithm.RS256, KeyUse.SIGNATURE);
		//RSAKey rsaKey2 = JWTHelper.createRSAKey(null, KeyUse.ENCRYPTION);

		return new JWKSet(Arrays.asList(rsaKey1));
	}

	public static String mockedTrustAnchorEntityConfigurationBad() throws Exception {
		JSONObject publicJwks = new JSONObject(createJWKSet().toJSONObject());

		JSONObject payload = new JSONObject()
			.put("iat", RPTestUtils.makeIssuedAt())
			.put("exp", RPTestUtils.makeExpiresOn())
			.put("iss", RPTestUtils.TRUST_ANCHOR)
			.put("sub", RPTestUtils.TRUST_ANCHOR)
			.put("jwks", publicJwks);

		JSONObject trustAnchorMetadata = new JSONObject()
			.put("contacts", JSONUtil.asJSONArray("ta@localhost"))
			.put("federation_fetch_endpoint", RPTestUtils.TRUST_ANCHOR + "fetch/")
			.put("federation_resolve_endpoint", RPTestUtils.TRUST_ANCHOR + "resolve/")
			.put("federation_status_endpoint", RPTestUtils.TRUST_ANCHOR + "trust_mask_status/")
			.put("homepage_uri", RPTestUtils.TRUST_ANCHOR)
			.put("name", "example TA")
			.put("federation_list_endpoint", RPTestUtils.TRUST_ANCHOR + "list/");

		payload.put(
			"metadata", new JSONObject().put("federation_entity", trustAnchorMetadata));

		JSONObject trustMarksIssuers = new JSONObject()
			.put(
				"https://www.spid.gov.it/certification/rp/public", JSONUtil.asJSONArray(
					"https://registry.spid.agid.gov.it",
					"https://public.intermediary.spid.it"))
			.put(
				"https://www.spid.gov.it/certification/rp/private", JSONUtil.asJSONArray(
					"https://registry.spid.agid.gov.it",
					"https://private.other.intermediary.it"))
			.put(
				"https://sgd.aa.it/onboarding", JSONUtil.asJSONArray(
					"https://sgd.aa.it"));

		payload.put("trust_marks_issuers", trustMarksIssuers);
		payload.put("constraints", new JSONObject().put("max_path_length", 1));

		JSONObject jwks = RPTestUtils.mockedTrustAnchorPrivateJWKS();

		return RPTestUtils.createJWS(payload, jwks);
	}


}
