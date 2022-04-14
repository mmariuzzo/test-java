package it.spid.cie.oidc.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import com.nimbusds.jose.jwk.JWKSet;

import it.spid.cie.oidc.helper.JWTHelper;
import it.spid.cie.oidc.test.util.RPTestUtils;
import it.spid.cie.oidc.util.JSONUtil;

public class TestEntityConfiguration {

	@Test
	public void testEntityConfigurationClass() {
		JWTHelper jwtHelper = null;
		EntityConfiguration ec  = null;

		boolean catched = false;

		try {
			jwtHelper = new JWTHelper(RPTestUtils.getOptions());

			String es = RPTestUtils.mockedSPIDProviderEntityStatement();

			ec = new EntityConfiguration(es, jwtHelper);
		}
		catch (Exception e) {
			catched = true;
		}

		assertFalse(catched);

		// Case: getter before processing

		catched = false;

		try {
			ec.getConstraint("test", 0);
			ec.getExp();
			ec.getExpiresOn();
			ec.getFederationFetchEndpoint();
			ec.getIssuedAt();
			ec.getIssuer();
			ec.getJwks();
			ec.getJWKSet();
			ec.getJwt();
			ec.getPayload();
			ec.getPayloadMetadata();
			ec.getSubject();
			ec.hasVerifiedBySuperiors();
			ec.addFailedDescendantStatement("test", new JSONObject());
		}
		catch (Exception e) {
			catched = true;
		}

		assertFalse(catched);
		assertFalse(ec.hasJWK("test"));
		assertFalse(ec.hasJWK(""));

		catched = false;
		EntityConfiguration ec2 = null;

		try {
			CachedEntityInfo cei = CachedEntityInfo.of(
				ec.getSubject(), ec.getIssuer(), ec.getExpiresOn(), ec.getIssuedAt(),
				ec.getPayload(), ec.getJwt());

			ec2 = EntityConfiguration.of(cei, jwtHelper);
		}
		catch (Exception e) {
			catched = true;
		}

		assertFalse(catched);
		assertNotNull(ec2);
		assertEquals(ec.getExp(), ec2.getExp());
	}

	@Test
	public void test_validateBySuperior() {
		EntityConfiguration ec = null;
		boolean catched = false;

		try {
			JWTHelper jwtHelper = new JWTHelper(RPTestUtils.getOptions());

			String es = RPTestUtils.mockedSPIDProviderEntityStatement();

			ec = new EntityConfiguration(es, jwtHelper);
		}
		catch (Exception e) {
			catched = true;
		}

		assertFalse(catched);
		assertNotNull(ec);

		catched = false;
		boolean res = false;

		try {
			res = ec.validateBySuperior("test", null);
		}
		catch (Exception e) {
			catched = true;
		}

		assertFalse(catched);
		assertFalse(res);
	}

	@Test
	public void test_validateByItself() {
		EntityConfiguration ec = null;
		boolean catched = false;

		try {
			JWTHelper jwtHelper = new JWTHelper(RPTestUtils.getOptions());

			String es = mockedSPIDProviderEntityStatement2();

			ec = new EntityConfiguration(es, jwtHelper);
		}
		catch (Exception e) {
			catched = true;
		}

		assertFalse(catched);
		assertNotNull(ec);

		catched = false;
		boolean res = false;

		try {
			res = ec.validateItself();
		}
		catch (Exception e) {
			catched = true;
		}

		assertFalse(catched);
		assertFalse(res);
	}

	@Test
	public void test_validateDescendant() {
		EntityConfiguration ec = null;
		boolean catched = false;

		try {
			JWTHelper jwtHelper = new JWTHelper(RPTestUtils.getOptions());

			String es = RPTestUtils.mockedSPIDProviderEntityStatement();

			ec = new EntityConfiguration(es, jwtHelper);
		}
		catch (Exception e) {
			catched = true;
		}

		assertFalse(catched);
		assertNotNull(ec);

		catched = false;
		boolean res = false;

		try {
			String es2 = mockedSPIDProviderEntityStatement3();

			res = ec.validateDescendant(es2);
		}
		catch (Exception e) {
			catched = true;
		}

		assertTrue(catched);
	}

	@Test
	public void test_validateBySuperiors() {
		JWTHelper jwtHelper = null;
		EntityConfiguration ec = null;
		boolean catched = false;

		try {
			jwtHelper = new JWTHelper(RPTestUtils.getOptions());

			String es = RPTestUtils.mockedSPIDProviderEntityStatement();

			ec = new EntityConfiguration(es, jwtHelper);
		}
		catch (Exception e) {
			catched = true;
		}

		assertFalse(catched);
		assertNotNull(ec);

		catched = false;
		Map<String, EntityConfiguration> res = null;

		try {
			String es2 = mockedTrustAnchorEntityConfigurationC1();

			List<EntityConfiguration> superiors = Arrays.asList(
				new EntityConfiguration(es2, jwtHelper));

			res = ec.validateBySuperiors(superiors);
		}
		catch (Exception e) {
			catched = true;
		}

		assertFalse(catched);
		assertTrue(res.size() == 0);
	}

	@Test
	public void test_getTrustMarksIssuers() {
		JWTHelper jwtHelper = null;
		EntityConfiguration ec = null;
		boolean catched = false;

		try {
			jwtHelper = new JWTHelper(RPTestUtils.getOptions());

			String es = mockedTrustAnchorEntityConfigurationC2();

			ec = new EntityConfiguration(es, jwtHelper);
		}
		catch (Exception e) {
			catched = true;
		}

		assertFalse(catched);
		assertNotNull(ec);

		catched = false;
		Map<String, Set<String>> res = null;

		try {
			res = ec.getTrustMarksIssuers();
		}
		catch (Exception e) {
			catched = true;
		}

		assertFalse(catched);
		assertTrue(res.size() == 3);
	}

	@Test
	public void test_hasConstraint() {
		JWTHelper jwtHelper = null;
		EntityConfiguration ec = null;
		boolean catched = false;

		try {
			jwtHelper = new JWTHelper(RPTestUtils.getOptions());

			String es = mockedTrustAnchorEntityConfigurationC2();

			ec = new EntityConfiguration(es, jwtHelper);
		}
		catch (Exception e) {
			catched = true;
		}

		assertFalse(catched);
		assertNotNull(ec);

		catched = false;
		boolean res = false;

		try {
			res = ec.hasConstraint("random");
		}
		catch (Exception e) {
			catched = true;
		}

		assertFalse(catched);
		assertFalse(res);
	}

	@Test
	public void test_getSuperiors() {
		JWTHelper jwtHelper = null;
		EntityConfiguration ec = null;
		boolean catched = false;

		try {
			jwtHelper = new JWTHelper(RPTestUtils.getOptions());

			String es = mockedSPIDProviderEntityStatement1();

			ec = new EntityConfiguration(es, jwtHelper);
		}
		catch (Exception e) {
			catched = true;
		}

		assertFalse(catched);
		assertNotNull(ec);

		catched = false;
		Map<String, EntityConfiguration> res = null;

		try {
			List<EntityConfiguration> superiorHints = new ArrayList<>();

			res = ec.getSuperiors(1, superiorHints);
		}
		catch (Exception e) {
			catched = true;
		}

		assertFalse(catched);
		assertTrue(res.size() == 0);
	}

	public static String mockedSPIDProviderEntityStatement1() throws Exception {
		JSONObject payload = new JSONObject()
			.put("iat", RPTestUtils.makeIssuedAt())
			.put("exp", RPTestUtils.makeExpiresOn())
			.put("iss", RPTestUtils.TRUST_ANCHOR)
			.put("sub", RPTestUtils.SPID_PROVIDER)
			.put("jwks", RPTestUtils.mockedSPIDProviderPublicJWKS());

		JSONObject providerPolicy = new JSONObject()
			.put(
				"subject_types_supported", new JSONObject()
					.put("value", JSONUtil.asJSONArray("pairwise")))
			.put(
				"id_token_signing_alg_values_supported", new JSONObject()
					.put(
						"subset_of", JSONUtil.asJSONArray(
							"RS256", "RS384", "RS512", "ES256", "ES384", "ES512")))
			.put(
				"userinfo_signing_alg_values_supported", new JSONObject()
					.put(
						"subset_of", JSONUtil.asJSONArray(
							"RS256", "RS384", "RS512", "ES256", "ES384", "ES512")))
			.put(
				"token_endpoint_auth_methods_supported", new JSONObject()
					.put("value", JSONUtil.asJSONArray("private_key_jwt")))
			.put(
				"userinfo_encryption_alg_values_supported", new JSONObject()
					.put(
						"subset_of", JSONUtil.asJSONArray(
							"RSA-OAEP", "RSA-OAEP-256", "ECDH-ES", "ECDH-ES+A128KW",
							"ECDH-ES+A192KW", "ECDH-ES+A256KW")))
			.put(
				"userinfo_encryption_enc_values_supported", new JSONObject()
					.put(
						"subset_of", JSONUtil.asJSONArray(
							"A128CBC-HS256", "A192CBC-HS384", "A256CBC-HS512", "A128GCM",
							"A192GCM", "A256GCM")))
			.put(
				"request_object_encryption_alg_values_supported", new JSONObject()
					.put(
						"subset_of", JSONUtil.asJSONArray(
							"RSA-OAEP", "RSA-OAEP-256", "ECDH-ES", "ECDH-ES+A128KW",
							"ECDH-ES+A192KW", "ECDH-ES+A256KW")))
			.put(
				"request_object_encryption_enc_values_supported", new JSONObject()
					.put(
						"subset_of", JSONUtil.asJSONArray(
							"A128CBC-HS256", "A192CBC-HS384", "A256CBC-HS512", "A128GCM",
							"A192GCM", "A256GCM")))
			.put(
				"request_object_signing_alg_values_supported", new JSONObject()
					.put(
						"subset_of", JSONUtil.asJSONArray(
							"RS256", "RS384", "RS512", "ES256", "ES384", "ES512")));

		payload.put(
			"metadata_policy", new JSONObject().put("openid_provider", providerPolicy));

		JSONArray trustMarks = new JSONArray(
			RPTestUtils.getContent("spid-op-trust-marks.json"));

		payload.put("trust_marks", trustMarks);

		payload.put("authority_hints", JSONUtil.asJSONArray(
			RPTestUtils.TRUST_ANCHOR + "1", RPTestUtils.TRUST_ANCHOR + "2",
			RPTestUtils.TRUST_ANCHOR));

		JSONObject jwks = RPTestUtils.mockedSPIDProviderPrivateJWKS();

		return RPTestUtils.createJWS(payload, jwks);
	}

	private String mockedSPIDProviderEntityStatement2() throws Exception {
		JWKSet jwkSet = RPTestUtils.createJWKSet();

		JSONObject publicJwks = new JSONObject(jwkSet.toJSONObject(true));
		JSONObject privateJwks = RPTestUtils.mockedSPIDProviderPrivateJWKS();

		return doMockedSPIDProviderEntityStatement(publicJwks, privateJwks);
	}

	private String mockedSPIDProviderEntityStatement3() throws Exception {
		JWKSet jwkSet = RPTestUtils.createJWKSet();

		JSONObject publicJwks = new JSONObject(jwkSet.toJSONObject(true));
		JSONObject privateJwks = new JSONObject(jwkSet.toJSONObject(false));

		return doMockedSPIDProviderEntityStatement(publicJwks, privateJwks);
	}

	private static String mockedTrustAnchorEntityConfigurationC1()
		throws Exception {

		JSONObject payload = new JSONObject()
			.put("iat", RPTestUtils.makeIssuedAt())
			.put("exp", RPTestUtils.makeExpiresOn())
			.put("iss", RPTestUtils.TRUST_ANCHOR)
			.put("sub", RPTestUtils.TRUST_ANCHOR)
			.put("jwks", RPTestUtils.mockedTrustAnchorPublicJWKS());

		JSONObject trustAnchorMetadata = new JSONObject()
			.put("contacts", JSONUtil.asJSONArray("ta@localhost"))
			//.put("federation_fetch_endpoint", RPTestUtils.TRUST_ANCHOR + "fetch/")
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

	private static String mockedTrustAnchorEntityConfigurationC2()
		throws Exception {

		JSONObject payload = new JSONObject()
			.put("iat", RPTestUtils.makeIssuedAt())
			.put("exp", RPTestUtils.makeExpiresOn())
			.put("iss", RPTestUtils.TRUST_ANCHOR)
			.put("sub", RPTestUtils.TRUST_ANCHOR)
			.put("jwks", RPTestUtils.mockedTrustAnchorPublicJWKS());

		JSONObject trustAnchorMetadata = new JSONObject()
			.put("contacts", JSONUtil.asJSONArray("ta@localhost"))
			//.put("federation_fetch_endpoint", RPTestUtils.TRUST_ANCHOR + "fetch/")
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
			.put("wrong-issuer", false)
			.put(
				"https://sgd.aa.it/onboarding", JSONUtil.asJSONArray(
					"https://sgd.aa.it"));

		payload.put("trust_marks_issuers", trustMarksIssuers);
		//payload.put("constraints", new JSONObject().put("max_path_length", 1));

		JSONObject jwks = RPTestUtils.mockedTrustAnchorPrivateJWKS();

		return RPTestUtils.createJWS(payload, jwks);
	}
	private String doMockedSPIDProviderEntityStatement(
			JSONObject publicJwks, JSONObject privateJwks)
		throws Exception {

		JSONObject payload = new JSONObject()
			.put("iat", RPTestUtils.makeIssuedAt())
			.put("exp", RPTestUtils.makeExpiresOn())
			.put("iss", RPTestUtils.TRUST_ANCHOR)
			.put("sub", RPTestUtils.SPID_PROVIDER)
			.put("jwks", publicJwks);

		JSONObject providerPolicy = new JSONObject()
			.put(
				"subject_types_supported", new JSONObject()
					.put("value", JSONUtil.asJSONArray("pairwise")))
			.put(
				"id_token_signing_alg_values_supported", new JSONObject()
					.put(
						"subset_of", JSONUtil.asJSONArray(
							"RS256", "RS384", "RS512", "ES256", "ES384", "ES512")))
			.put(
				"userinfo_signing_alg_values_supported", new JSONObject()
					.put(
						"subset_of", JSONUtil.asJSONArray(
							"RS256", "RS384", "RS512", "ES256", "ES384", "ES512")))
			.put(
				"token_endpoint_auth_methods_supported", new JSONObject()
					.put("value", JSONUtil.asJSONArray("private_key_jwt")))
			.put(
				"userinfo_encryption_alg_values_supported", new JSONObject()
					.put(
						"subset_of", JSONUtil.asJSONArray(
							"RSA-OAEP", "RSA-OAEP-256", "ECDH-ES", "ECDH-ES+A128KW",
							"ECDH-ES+A192KW", "ECDH-ES+A256KW")))
			.put(
				"userinfo_encryption_enc_values_supported", new JSONObject()
					.put(
						"subset_of", JSONUtil.asJSONArray(
							"A128CBC-HS256", "A192CBC-HS384", "A256CBC-HS512", "A128GCM",
							"A192GCM", "A256GCM")))
			.put(
				"request_object_encryption_alg_values_supported", new JSONObject()
					.put(
						"subset_of", JSONUtil.asJSONArray(
							"RSA-OAEP", "RSA-OAEP-256", "ECDH-ES", "ECDH-ES+A128KW",
							"ECDH-ES+A192KW", "ECDH-ES+A256KW")))
			.put(
				"request_object_encryption_enc_values_supported", new JSONObject()
					.put(
						"subset_of", JSONUtil.asJSONArray(
							"A128CBC-HS256", "A192CBC-HS384", "A256CBC-HS512", "A128GCM",
							"A192GCM", "A256GCM")))
			.put(
				"request_object_signing_alg_values_supported", new JSONObject()
					.put(
						"subset_of", JSONUtil.asJSONArray(
							"RS256", "RS384", "RS512", "ES256", "ES384", "ES512")));

		payload.put(
			"metadata_policy", new JSONObject().put("openid_provider", providerPolicy));

		JSONArray trustMarks = new JSONArray(
			RPTestUtils.getContent("spid-op-trust-marks.json"));

		payload.put("trust_marks", trustMarks);

		return RPTestUtils.createJWS(payload, privateJwks);
	}


}
