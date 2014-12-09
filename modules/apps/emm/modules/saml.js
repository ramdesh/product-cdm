/*
 * *
 *  *  Copyright (c) 2005-2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *  *
 *  *  Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  *  You may obtain a copy of the License at
 *  *
 *  *        http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *  Unless required by applicable law or agreed to in writing, software
 *  *  distributed under the License is distributed on an "AS IS" BASIS,
 *  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  See the License for the specific language governing permissions and
 *  *  limitations under the License.
 *
 */

var saml = saml || {};
(function () {
    var SAML_RESPONSE_TOKEN_SESSION_KEY = "SAML_TOKEN";
    var SAML_ASSERTION_TOKEN_SESSION_KEY = "SAML_ASSERTION_TOKEN";
    var SSO_NAME = "SSORelyingParty.Name";

    var getToken = function (){
        if(session.get(SAML_RESPONSE_TOKEN_SESSION_KEY)){
            return session.get(SAML_RESPONSE_TOKEN_SESSION_KEY);
        } else if(session.get(SAML_ASSERTION_TOKEN_SESSION_KEY)){
            return session.get(SAML_ASSERTION_TOKEN_SESSION_KEY);
        } else {
            return null;
        }
    };

    saml.getBackendCookie = function (samlToken) {
        var token = getToken();
        var token = null;
        var encodedToken = token && token.replace(/>/g, '&gt;').replace(/</g,'&lt;');
        var xhr = new XMLHttpRequest();
        xhr.setRequestHeader('SOAPAction', 'urn:login');
        xhr.setRequestHeader('Content-Type', 'application/soap+xml');
        var endPoint = "https://localhost:9443/admin/services/"+"SAML2SSOAuthenticationService";
        xhr.open("POST", endPoint);
        var payload = '<soap:Envelope xmlns:soap="http://www.w3.org/2003/05/soap-envelope" xmlns:sso="http://sso.saml2.authenticator.identity.carbon.wso2.org" xmlns:xsd="http://dto.sso.saml2.authenticator.identity.carbon.wso2.org/xsd"><soap:Header/><soap:Body><sso:login><sso:authDto><xsd:response>'+samlToken+'</xsd:response></sso:authDto></sso:login></soap:Body></soap:Envelope>';
        xhr.send(payload);
        var cookieString = xhr.getResponseHeader("Set-Cookie");
        return cookieString;
    };
}());

