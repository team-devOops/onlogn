---
title: O(NlogN) API v1 v1.0.0-draft
language_tabs:
  - shell: Shell
  - http: HTTP
  - javascript: JavaScript
  - ruby: Ruby
  - python: Python
  - php: PHP
  - java: Java
  - go: Go
toc_footers: []
includes: []
search: true
highlight_theme: darkula
headingLevel: 2

---

<!-- Generator: Widdershins v4.0.1 -->

<h1 id="o-nlogn-api-v1">O(NlogN) API v1 v1.0.0-draft</h1>

> Scroll down for code samples, example requests and responses. Select a language for code samples from the tabs above or the mobile navigation menu.

O(NlogN) API 계약 스켈레톤.
이 기준선은 루트 메타데이터, 공통 보안, 재사용 가능한 컴포넌트를 정의한다.

Base URLs:

* <a href="https://api.onlogn.com">https://api.onlogn.com</a>

* <a href="http://localhost:3000">http://localhost:3000</a>

Web: <a href="https://api.onlogn.com">O(NlogN) API Team</a> 

# Authentication

- HTTP Authentication, scheme: bearer 보호 엔드포인트를 위한 access token 전달 방식.
auth 엔드포인트에서는 토큰을 JSON 응답 body로 전달한다.

<h1 id="o-nlogn-api-v1-auth">auth</h1>

인증 및 세션 작업.

## exchangeGithubOAuthCode

<a id="opIdexchangeGithubOAuthCode"></a>

> Code samples

```shell
# You can also use wget
curl -X POST https://api.onlogn.com/api/v1/auth/oauth/github/exchange \
  -H 'Content-Type: application/json' \
  -H 'Accept: application/json'

```

```http
POST https://api.onlogn.com/api/v1/auth/oauth/github/exchange HTTP/1.1
Host: api.onlogn.com
Content-Type: application/json
Accept: application/json

```

```javascript
const inputBody = '{
  "data": {
    "code": "gh_oauth_code_123"
  }
}';
const headers = {
  'Content-Type':'application/json',
  'Accept':'application/json'
};

fetch('https://api.onlogn.com/api/v1/auth/oauth/github/exchange',
{
  method: 'POST',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

```ruby
require 'rest-client'
require 'json'

headers = {
  'Content-Type' => 'application/json',
  'Accept' => 'application/json'
}

result = RestClient.post 'https://api.onlogn.com/api/v1/auth/oauth/github/exchange',
  params: {
  }, headers: headers

p JSON.parse(result)

```

```python
import requests
headers = {
  'Content-Type': 'application/json',
  'Accept': 'application/json'
}

r = requests.post('https://api.onlogn.com/api/v1/auth/oauth/github/exchange', headers = headers)

print(r.json())

```

```php
<?php

require 'vendor/autoload.php';

$headers = array(
    'Content-Type' => 'application/json',
    'Accept' => 'application/json',
);

$client = new \GuzzleHttp\Client();

// Define array of request body.
$request_body = array();

try {
    $response = $client->request('POST','https://api.onlogn.com/api/v1/auth/oauth/github/exchange', array(
        'headers' => $headers,
        'json' => $request_body,
       )
    );
    print_r($response->getBody()->getContents());
 }
 catch (\GuzzleHttp\Exception\BadResponseException $e) {
    // handle exception or api errors.
    print_r($e->getMessage());
 }

 // ...

```

```java
URL obj = new URL("https://api.onlogn.com/api/v1/auth/oauth/github/exchange");
HttpURLConnection con = (HttpURLConnection) obj.openConnection();
con.setRequestMethod("POST");
int responseCode = con.getResponseCode();
BufferedReader in = new BufferedReader(
    new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer response = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
}
in.close();
System.out.println(response.toString());

```

```go
package main

import (
       "bytes"
       "net/http"
)

func main() {

    headers := map[string][]string{
        "Content-Type": []string{"application/json"},
        "Accept": []string{"application/json"},
    }

    data := bytes.NewBuffer([]byte{jsonReq})
    req, err := http.NewRequest("POST", "https://api.onlogn.com/api/v1/auth/oauth/github/exchange", data)
    req.Header = headers

    client := &http.Client{}
    resp, err := client.Do(req)
    // ...
}

```

`POST /api/v1/auth/oauth/github/exchange`

*GitHub OAuth code 교환*

GitHub OAuth authorization code를 새 access/refresh token으로 교환한다.
토큰은 JSON 응답 body 필드로만 반환되며 cookies에 중복되지 않는다.

> Body parameter

```json
{
  "data": {
    "code": "gh_oauth_code_123"
  }
}
```

<h3 id="exchangegithuboauthcode-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|body|body|[AuthGithubExchangeRequestEnvelope](#schemaauthgithubexchangerequestenvelope)|true|none|

> Example responses

> 200 Response

```json
{
  "data": {
    "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.example",
    "refresh_token": "rf_01JZ4T3Q8R9A2F4M7N6",
    "token_type": "Bearer",
    "expires_in": 900,
    "refresh_expires_in": 1209600
  }
}
```

> 400 Response

```json
{
  "type": "https://api.onlogn.com/problems/bad-request",
  "title": "Bad Request",
  "status": 400,
  "detail": "Request body is malformed or missing required fields.",
  "instance": "/api/v1/auth/oauth/github/exchange/req_01JZ4T3Q8R9A2F4M7N6"
}
```

> 401 Response

```json
{
  "type": "https://api.onlogn.com/problems/authentication-required",
  "title": "Authentication Required",
  "status": 401,
  "detail": "Credential is missing, invalid, expired, or already rotated.",
  "instance": "/api/v1/auth/refresh/req_01JZ4T3Q8R9A2F4M7N6"
}
```

> 429 Response

```json
{
  "type": "https://api.onlogn.com/problems/rate-limit-exceeded",
  "title": "Rate Limit Exceeded",
  "status": 429,
  "detail": "Too many requests. Retry after 30 seconds.",
  "instance": "/api/v1/auth/refresh/req_01JZ4T3Q8R9A2F4M7N6",
  "retry_after_seconds": 30
}
```

<h3 id="exchangegithuboauthcode-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|Access 및 refresh token 발급 성공.|[AuthTokenSuccessEnvelope](#schemaauthtokensuccessenvelope)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|형식이 잘못된 요청 payload 또는 미지원 body 형태.|[Problem](#schemaproblem)|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|누락, 만료, 무효, 또는 이미 회전된 자격 증명.|[Problem](#schemaproblem)|
|429|[Too Many Requests](https://tools.ietf.org/html/rfc6585#section-4)|스로틀링 정책에 의해 요청이 거부됨.|[Problem](#schemaproblem)|

<aside class="success">
This operation does not require authentication
</aside>

## rotateRefreshToken

<a id="opIdrotateRefreshToken"></a>

> Code samples

```shell
# You can also use wget
curl -X POST https://api.onlogn.com/api/v1/auth/refresh \
  -H 'Content-Type: application/json' \
  -H 'Accept: application/json'

```

```http
POST https://api.onlogn.com/api/v1/auth/refresh HTTP/1.1
Host: api.onlogn.com
Content-Type: application/json
Accept: application/json

```

```javascript
const inputBody = '{
  "data": {
    "refresh_token": "rf_01JZ4T3Q8R9A2F4M7N6"
  }
}';
const headers = {
  'Content-Type':'application/json',
  'Accept':'application/json'
};

fetch('https://api.onlogn.com/api/v1/auth/refresh',
{
  method: 'POST',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

```ruby
require 'rest-client'
require 'json'

headers = {
  'Content-Type' => 'application/json',
  'Accept' => 'application/json'
}

result = RestClient.post 'https://api.onlogn.com/api/v1/auth/refresh',
  params: {
  }, headers: headers

p JSON.parse(result)

```

```python
import requests
headers = {
  'Content-Type': 'application/json',
  'Accept': 'application/json'
}

r = requests.post('https://api.onlogn.com/api/v1/auth/refresh', headers = headers)

print(r.json())

```

```php
<?php

require 'vendor/autoload.php';

$headers = array(
    'Content-Type' => 'application/json',
    'Accept' => 'application/json',
);

$client = new \GuzzleHttp\Client();

// Define array of request body.
$request_body = array();

try {
    $response = $client->request('POST','https://api.onlogn.com/api/v1/auth/refresh', array(
        'headers' => $headers,
        'json' => $request_body,
       )
    );
    print_r($response->getBody()->getContents());
 }
 catch (\GuzzleHttp\Exception\BadResponseException $e) {
    // handle exception or api errors.
    print_r($e->getMessage());
 }

 // ...

```

```java
URL obj = new URL("https://api.onlogn.com/api/v1/auth/refresh");
HttpURLConnection con = (HttpURLConnection) obj.openConnection();
con.setRequestMethod("POST");
int responseCode = con.getResponseCode();
BufferedReader in = new BufferedReader(
    new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer response = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
}
in.close();
System.out.println(response.toString());

```

```go
package main

import (
       "bytes"
       "net/http"
)

func main() {

    headers := map[string][]string{
        "Content-Type": []string{"application/json"},
        "Accept": []string{"application/json"},
    }

    data := bytes.NewBuffer([]byte{jsonReq})
    req, err := http.NewRequest("POST", "https://api.onlogn.com/api/v1/auth/refresh", data)
    req.Header = headers

    client := &http.Client{}
    resp, err := client.Do(req)
    // ...
}

```

`POST /api/v1/auth/refresh`

*Refresh token 회전*

성공 호출마다 refresh token을 회전하고, 이전에 제시된 token을 즉시 무효화한다.
이미 무효화된 refresh token을 재사용하면 401 RFC9457 problem details로 실패한다.

> Body parameter

```json
{
  "data": {
    "refresh_token": "rf_01JZ4T3Q8R9A2F4M7N6"
  }
}
```

<h3 id="rotaterefreshtoken-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|body|body|[AuthRefreshRequestEnvelope](#schemaauthrefreshrequestenvelope)|true|none|

> Example responses

> 200 Response

```json
{
  "data": {
    "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.example",
    "refresh_token": "rf_01JZ4T3Q8R9A2F4M7N6",
    "token_type": "Bearer",
    "expires_in": 900,
    "refresh_expires_in": 1209600
  }
}
```

> 400 Response

```json
{
  "type": "https://api.onlogn.com/problems/bad-request",
  "title": "Bad Request",
  "status": 400,
  "detail": "Request body is malformed or missing required fields.",
  "instance": "/api/v1/auth/oauth/github/exchange/req_01JZ4T3Q8R9A2F4M7N6"
}
```

> 401 Response

```json
{
  "type": "https://api.onlogn.com/problems/authentication-required",
  "title": "Authentication Required",
  "status": 401,
  "detail": "Credential is missing, invalid, expired, or already rotated.",
  "instance": "/api/v1/auth/refresh/req_01JZ4T3Q8R9A2F4M7N6"
}
```

> 429 Response

```json
{
  "type": "https://api.onlogn.com/problems/rate-limit-exceeded",
  "title": "Rate Limit Exceeded",
  "status": 429,
  "detail": "Too many requests. Retry after 30 seconds.",
  "instance": "/api/v1/auth/refresh/req_01JZ4T3Q8R9A2F4M7N6",
  "retry_after_seconds": 30
}
```

<h3 id="rotaterefreshtoken-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|회전 후 새 access 및 refresh token 발급.|[AuthTokenSuccessEnvelope](#schemaauthtokensuccessenvelope)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|형식이 잘못된 요청 payload 또는 미지원 body 형태.|[Problem](#schemaproblem)|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|누락, 만료, 무효, 또는 이미 회전된 자격 증명.|[Problem](#schemaproblem)|
|429|[Too Many Requests](https://tools.ietf.org/html/rfc6585#section-4)|스로틀링 정책에 의해 요청이 거부됨.|[Problem](#schemaproblem)|

<aside class="success">
This operation does not require authentication
</aside>

## logoutCurrentDevice

<a id="opIdlogoutCurrentDevice"></a>

> Code samples

```shell
# You can also use wget
curl -X POST https://api.onlogn.com/api/v1/auth/logout \
  -H 'Content-Type: application/json' \
  -H 'Accept: application/problem+json'

```

```http
POST https://api.onlogn.com/api/v1/auth/logout HTTP/1.1
Host: api.onlogn.com
Content-Type: application/json
Accept: application/problem+json

```

```javascript
const inputBody = '{
  "data": {
    "refresh_token": "rf_01JZ4T3Q8R9A2F4M7N6"
  }
}';
const headers = {
  'Content-Type':'application/json',
  'Accept':'application/problem+json'
};

fetch('https://api.onlogn.com/api/v1/auth/logout',
{
  method: 'POST',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

```ruby
require 'rest-client'
require 'json'

headers = {
  'Content-Type' => 'application/json',
  'Accept' => 'application/problem+json'
}

result = RestClient.post 'https://api.onlogn.com/api/v1/auth/logout',
  params: {
  }, headers: headers

p JSON.parse(result)

```

```python
import requests
headers = {
  'Content-Type': 'application/json',
  'Accept': 'application/problem+json'
}

r = requests.post('https://api.onlogn.com/api/v1/auth/logout', headers = headers)

print(r.json())

```

```php
<?php

require 'vendor/autoload.php';

$headers = array(
    'Content-Type' => 'application/json',
    'Accept' => 'application/problem+json',
);

$client = new \GuzzleHttp\Client();

// Define array of request body.
$request_body = array();

try {
    $response = $client->request('POST','https://api.onlogn.com/api/v1/auth/logout', array(
        'headers' => $headers,
        'json' => $request_body,
       )
    );
    print_r($response->getBody()->getContents());
 }
 catch (\GuzzleHttp\Exception\BadResponseException $e) {
    // handle exception or api errors.
    print_r($e->getMessage());
 }

 // ...

```

```java
URL obj = new URL("https://api.onlogn.com/api/v1/auth/logout");
HttpURLConnection con = (HttpURLConnection) obj.openConnection();
con.setRequestMethod("POST");
int responseCode = con.getResponseCode();
BufferedReader in = new BufferedReader(
    new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer response = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
}
in.close();
System.out.println(response.toString());

```

```go
package main

import (
       "bytes"
       "net/http"
)

func main() {

    headers := map[string][]string{
        "Content-Type": []string{"application/json"},
        "Accept": []string{"application/problem+json"},
    }

    data := bytes.NewBuffer([]byte{jsonReq})
    req, err := http.NewRequest("POST", "https://api.onlogn.com/api/v1/auth/logout", data)
    req.Header = headers

    client := &http.Client{}
    resp, err := client.Do(req)
    // ...
}

```

`POST /api/v1/auth/logout`

*현재 디바이스 로그아웃*

제공된 refresh token이 나타내는 현재 디바이스 세션만 폐기한다.
v1 엔드포인트는 전체 디바이스 또는 계정 단위 로그아웃을 수행하지 않는다.

> Body parameter

```json
{
  "data": {
    "refresh_token": "rf_01JZ4T3Q8R9A2F4M7N6"
  }
}
```

<h3 id="logoutcurrentdevice-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|body|body|[AuthLogoutRequestEnvelope](#schemaauthlogoutrequestenvelope)|true|none|

> Example responses

> 400 Response

```json
{
  "type": "https://api.onlogn.com/problems/bad-request",
  "title": "Bad Request",
  "status": 400,
  "detail": "Request body is malformed or missing required fields.",
  "instance": "/api/v1/auth/oauth/github/exchange/req_01JZ4T3Q8R9A2F4M7N6"
}
```

> 401 Response

```json
{
  "type": "https://api.onlogn.com/problems/authentication-required",
  "title": "Authentication Required",
  "status": 401,
  "detail": "Credential is missing, invalid, expired, or already rotated.",
  "instance": "/api/v1/auth/refresh/req_01JZ4T3Q8R9A2F4M7N6"
}
```

> 429 Response

```json
{
  "type": "https://api.onlogn.com/problems/rate-limit-exceeded",
  "title": "Rate Limit Exceeded",
  "status": 429,
  "detail": "Too many requests. Retry after 30 seconds.",
  "instance": "/api/v1/auth/refresh/req_01JZ4T3Q8R9A2F4M7N6",
  "retry_after_seconds": 30
}
```

<h3 id="logoutcurrentdevice-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|204|[No Content](https://tools.ietf.org/html/rfc7231#section-6.3.5)|현재 디바이스 세션이 폐기되었거나 이미 없음.|None|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|형식이 잘못된 요청 payload 또는 미지원 body 형태.|[Problem](#schemaproblem)|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|누락, 만료, 무효, 또는 이미 회전된 자격 증명.|[Problem](#schemaproblem)|
|429|[Too Many Requests](https://tools.ietf.org/html/rfc6585#section-4)|스로틀링 정책에 의해 요청이 거부됨.|[Problem](#schemaproblem)|

### Response Headers

|Status|Header|Type|Format|Description|
|---|---|---|---|---|
|204|X-Request-Id|string||이 성공 로그아웃 요청의 추적 id.|

<aside class="success">
This operation does not require authentication
</aside>

<h1 id="o-nlogn-api-v1-users">users</h1>

인증 사용자 프로필 작업.

## getCurrentUser

<a id="opIdgetCurrentUser"></a>

> Code samples

```shell
# You can also use wget
curl -X GET https://api.onlogn.com/api/v1/users/me \
  -H 'Accept: application/json' \
  -H 'Authorization: Bearer {access-token}'

```

```http
GET https://api.onlogn.com/api/v1/users/me HTTP/1.1
Host: api.onlogn.com
Accept: application/json

```

```javascript

const headers = {
  'Accept':'application/json',
  'Authorization':'Bearer {access-token}'
};

fetch('https://api.onlogn.com/api/v1/users/me',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

```ruby
require 'rest-client'
require 'json'

headers = {
  'Accept' => 'application/json',
  'Authorization' => 'Bearer {access-token}'
}

result = RestClient.get 'https://api.onlogn.com/api/v1/users/me',
  params: {
  }, headers: headers

p JSON.parse(result)

```

```python
import requests
headers = {
  'Accept': 'application/json',
  'Authorization': 'Bearer {access-token}'
}

r = requests.get('https://api.onlogn.com/api/v1/users/me', headers = headers)

print(r.json())

```

```php
<?php

require 'vendor/autoload.php';

$headers = array(
    'Accept' => 'application/json',
    'Authorization' => 'Bearer {access-token}',
);

$client = new \GuzzleHttp\Client();

// Define array of request body.
$request_body = array();

try {
    $response = $client->request('GET','https://api.onlogn.com/api/v1/users/me', array(
        'headers' => $headers,
        'json' => $request_body,
       )
    );
    print_r($response->getBody()->getContents());
 }
 catch (\GuzzleHttp\Exception\BadResponseException $e) {
    // handle exception or api errors.
    print_r($e->getMessage());
 }

 // ...

```

```java
URL obj = new URL("https://api.onlogn.com/api/v1/users/me");
HttpURLConnection con = (HttpURLConnection) obj.openConnection();
con.setRequestMethod("GET");
int responseCode = con.getResponseCode();
BufferedReader in = new BufferedReader(
    new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer response = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
}
in.close();
System.out.println(response.toString());

```

```go
package main

import (
       "bytes"
       "net/http"
)

func main() {

    headers := map[string][]string{
        "Accept": []string{"application/json"},
        "Authorization": []string{"Bearer {access-token}"},
    }

    data := bytes.NewBuffer([]byte{jsonReq})
    req, err := http.NewRequest("GET", "https://api.onlogn.com/api/v1/users/me", data)
    req.Header = headers

    client := &http.Client{}
    resp, err := client.Do(req)
    // ...
}

```

`GET /api/v1/users/me`

*현재 인증 사용자 조회*

private 컨텍스트에서 인증 사용자의 전체 프로필 payload를 반환한다.

> Example responses

> 200 Response

```json
{
  "data": {
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "profile_slug": "johndoe",
    "email": "user@example.com",
    "display_name": "John Doe",
    "avatar_url": "https://cdn.onlogn.com/avatars/user.png",
    "timezone": "UTC",
    "created_at": "2025-01-01T00:00:00Z",
    "updated_at": "2025-01-01T01:00:00Z"
  }
}
```

> RFC9457 problem details 응답.

```json
{
  "type": "https://api.onlogn.com/problems/authentication-required",
  "title": "Authentication Required",
  "status": 401,
  "detail": "Access token is missing or expired.",
  "instance": "/api/v1/users/me/req_01JZ4T3Q8R9A2F4M7N6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/permission-denied",
  "title": "Permission Denied",
  "status": 403,
  "detail": "You cannot access another user's private resource.",
  "instance": "/api/v1/groups/01ARZ3NDEKTSV4RRFFQ69G5FAV8/req_01JZ4T4H1X2B3C4D5E6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/resource-not-found",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "Requested resource does not exist.",
  "instance": "/api/v1/tasks/01ARZ3NDEKTSV4RRFFQ69G5FAV9/req_01JZ4T59K8L7M6N5P4Q"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/internal-server-error",
  "title": "Internal Server Error",
  "status": 500,
  "detail": "An unexpected error occurred while processing the request.",
  "instance": "/api/v1/groups/req_01JZ4T8V6W5X4Y3Z2A1",
  "request_id": "req_01JZ4T8V6W5X4Y3Z2A1"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/authentication-required",
  "title": "Authentication Required",
  "status": 401,
  "detail": "Access token is missing or expired.",
  "instance": "/api/v1/users/me/req_01JZ4T3Q8R9A2F4M7N6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/permission-denied",
  "title": "Permission Denied",
  "status": 403,
  "detail": "You cannot access another user's private resource.",
  "instance": "/api/v1/groups/01ARZ3NDEKTSV4RRFFQ69G5FAV8/req_01JZ4T4H1X2B3C4D5E6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/resource-not-found",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "Requested resource does not exist.",
  "instance": "/api/v1/tasks/01ARZ3NDEKTSV4RRFFQ69G5FAV9/req_01JZ4T59K8L7M6N5P4Q"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/internal-server-error",
  "title": "Internal Server Error",
  "status": 500,
  "detail": "An unexpected error occurred while processing the request.",
  "instance": "/api/v1/groups/req_01JZ4T8V6W5X4Y3Z2A1",
  "request_id": "req_01JZ4T8V6W5X4Y3Z2A1"
}
```

<h3 id="getcurrentuser-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|현재 사용자 레코드.|Inline|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|RFC9457 problem details 응답.|[Problem](#schemaproblem)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|RFC9457 problem details 응답.|[Problem](#schemaproblem)|

<h3 id="getcurrentuser-responseschema">Response Schema</h3>

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

<h1 id="o-nlogn-api-v1-profiles">profiles</h1>

공개 프로필 및 프로필 범위 조회 작업.

## getProfileBySlug

<a id="opIdgetProfileBySlug"></a>

> Code samples

```shell
# You can also use wget
curl -X GET https://api.onlogn.com/api/v1/profiles/{slug} \
  -H 'Accept: application/json'

```

```http
GET https://api.onlogn.com/api/v1/profiles/{slug} HTTP/1.1
Host: api.onlogn.com
Accept: application/json

```

```javascript

const headers = {
  'Accept':'application/json'
};

fetch('https://api.onlogn.com/api/v1/profiles/{slug}',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

```ruby
require 'rest-client'
require 'json'

headers = {
  'Accept' => 'application/json'
}

result = RestClient.get 'https://api.onlogn.com/api/v1/profiles/{slug}',
  params: {
  }, headers: headers

p JSON.parse(result)

```

```python
import requests
headers = {
  'Accept': 'application/json'
}

r = requests.get('https://api.onlogn.com/api/v1/profiles/{slug}', headers = headers)

print(r.json())

```

```php
<?php

require 'vendor/autoload.php';

$headers = array(
    'Accept' => 'application/json',
);

$client = new \GuzzleHttp\Client();

// Define array of request body.
$request_body = array();

try {
    $response = $client->request('GET','https://api.onlogn.com/api/v1/profiles/{slug}', array(
        'headers' => $headers,
        'json' => $request_body,
       )
    );
    print_r($response->getBody()->getContents());
 }
 catch (\GuzzleHttp\Exception\BadResponseException $e) {
    // handle exception or api errors.
    print_r($e->getMessage());
 }

 // ...

```

```java
URL obj = new URL("https://api.onlogn.com/api/v1/profiles/{slug}");
HttpURLConnection con = (HttpURLConnection) obj.openConnection();
con.setRequestMethod("GET");
int responseCode = con.getResponseCode();
BufferedReader in = new BufferedReader(
    new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer response = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
}
in.close();
System.out.println(response.toString());

```

```go
package main

import (
       "bytes"
       "net/http"
)

func main() {

    headers := map[string][]string{
        "Accept": []string{"application/json"},
    }

    data := bytes.NewBuffer([]byte{jsonReq})
    req, err := http.NewRequest("GET", "https://api.onlogn.com/api/v1/profiles/{slug}", data)
    req.Header = headers

    client := &http.Client{}
    resp, err := client.Do(req)
    // ...
}

```

`GET /api/v1/profiles/{slug}`

*slug로 공개 프로필 조회*

지정된 slug에 대해 공개 allowlist 프로필 객체를 반환한다.

<h3 id="getprofilebyslug-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|slug|path|string|true|공개 프로필 slug.|

> Example responses

> 200 Response

```json
{
  "data": {
    "profile_slug": "johndoe",
    "display_name": "John Doe",
    "avatar_url": "https://cdn.onlogn.com/avatars/user.png",
    "bio": "Product designer focused on clear systems.",
    "timezone": "UTC",
    "created_at": "2025-01-01T00:00:00Z",
    "stats_summary": {
      "total_tasks": 12,
      "done_tasks": 8,
      "in_progress_tasks": 3,
      "todo_tasks": 1
    }
  }
}
```

> RFC9457 problem details 응답.

```json
{
  "type": "https://api.onlogn.com/problems/authentication-required",
  "title": "Authentication Required",
  "status": 401,
  "detail": "Access token is missing or expired.",
  "instance": "/api/v1/users/me/req_01JZ4T3Q8R9A2F4M7N6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/permission-denied",
  "title": "Permission Denied",
  "status": 403,
  "detail": "You cannot access another user's private resource.",
  "instance": "/api/v1/groups/01ARZ3NDEKTSV4RRFFQ69G5FAV8/req_01JZ4T4H1X2B3C4D5E6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/resource-not-found",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "Requested resource does not exist.",
  "instance": "/api/v1/tasks/01ARZ3NDEKTSV4RRFFQ69G5FAV9/req_01JZ4T59K8L7M6N5P4Q"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/internal-server-error",
  "title": "Internal Server Error",
  "status": 500,
  "detail": "An unexpected error occurred while processing the request.",
  "instance": "/api/v1/groups/req_01JZ4T8V6W5X4Y3Z2A1",
  "request_id": "req_01JZ4T8V6W5X4Y3Z2A1"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/authentication-required",
  "title": "Authentication Required",
  "status": 401,
  "detail": "Access token is missing or expired.",
  "instance": "/api/v1/users/me/req_01JZ4T3Q8R9A2F4M7N6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/permission-denied",
  "title": "Permission Denied",
  "status": 403,
  "detail": "You cannot access another user's private resource.",
  "instance": "/api/v1/groups/01ARZ3NDEKTSV4RRFFQ69G5FAV8/req_01JZ4T4H1X2B3C4D5E6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/resource-not-found",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "Requested resource does not exist.",
  "instance": "/api/v1/tasks/01ARZ3NDEKTSV4RRFFQ69G5FAV9/req_01JZ4T59K8L7M6N5P4Q"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/internal-server-error",
  "title": "Internal Server Error",
  "status": 500,
  "detail": "An unexpected error occurred while processing the request.",
  "instance": "/api/v1/groups/req_01JZ4T8V6W5X4Y3Z2A1",
  "request_id": "req_01JZ4T8V6W5X4Y3Z2A1"
}
```

<h3 id="getprofilebyslug-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|공개 프로필 payload.|Inline|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|RFC9457 problem details 응답.|[Problem](#schemaproblem)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|RFC9457 problem details 응답.|[Problem](#schemaproblem)|

<h3 id="getprofilebyslug-responseschema">Response Schema</h3>

<aside class="success">
This operation does not require authentication
</aside>

## listProfileTasks

<a id="opIdlistProfileTasks"></a>

> Code samples

```shell
# You can also use wget
curl -X GET https://api.onlogn.com/api/v1/profiles/{slug}/tasks \
  -H 'Accept: application/json'

```

```http
GET https://api.onlogn.com/api/v1/profiles/{slug}/tasks HTTP/1.1
Host: api.onlogn.com
Accept: application/json

```

```javascript

const headers = {
  'Accept':'application/json'
};

fetch('https://api.onlogn.com/api/v1/profiles/{slug}/tasks',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

```ruby
require 'rest-client'
require 'json'

headers = {
  'Accept' => 'application/json'
}

result = RestClient.get 'https://api.onlogn.com/api/v1/profiles/{slug}/tasks',
  params: {
  }, headers: headers

p JSON.parse(result)

```

```python
import requests
headers = {
  'Accept': 'application/json'
}

r = requests.get('https://api.onlogn.com/api/v1/profiles/{slug}/tasks', headers = headers)

print(r.json())

```

```php
<?php

require 'vendor/autoload.php';

$headers = array(
    'Accept' => 'application/json',
);

$client = new \GuzzleHttp\Client();

// Define array of request body.
$request_body = array();

try {
    $response = $client->request('GET','https://api.onlogn.com/api/v1/profiles/{slug}/tasks', array(
        'headers' => $headers,
        'json' => $request_body,
       )
    );
    print_r($response->getBody()->getContents());
 }
 catch (\GuzzleHttp\Exception\BadResponseException $e) {
    // handle exception or api errors.
    print_r($e->getMessage());
 }

 // ...

```

```java
URL obj = new URL("https://api.onlogn.com/api/v1/profiles/{slug}/tasks");
HttpURLConnection con = (HttpURLConnection) obj.openConnection();
con.setRequestMethod("GET");
int responseCode = con.getResponseCode();
BufferedReader in = new BufferedReader(
    new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer response = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
}
in.close();
System.out.println(response.toString());

```

```go
package main

import (
       "bytes"
       "net/http"
)

func main() {

    headers := map[string][]string{
        "Accept": []string{"application/json"},
    }

    data := bytes.NewBuffer([]byte{jsonReq})
    req, err := http.NewRequest("GET", "https://api.onlogn.com/api/v1/profiles/{slug}/tasks", data)
    req.Header = headers

    client := &http.Client{}
    resp, err := client.Do(req)
    // ...
}

```

`GET /api/v1/profiles/{slug}/tasks`

*profile slug 기준 공개 task 목록 조회*

지정된 profile slug의 공개 task만 반환한다.
서버가 public visibility 경계를 강제한다.

<h3 id="listprofiletasks-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|slug|path|string|true|공개 프로필 slug.|
|offset|query|integer|false|목록 페이지네이션용 0 기반 offset.|
|limit|query|integer|false|목록 페이지네이션용 페이지 크기.|
|status|query|[TaskStatus](#schemataskstatus)|false|none|
|group_id|query|any|false|그룹 미지정 task를 위한 UUID group id 또는 리터럴 `null`.|
|due_date_from|query|string(date)|false|none|
|due_date_to|query|string(date)|false|none|
|sort|query|string|false|v1에서는 `created_at desc`만 허용한다.|

#### Enumerated Values

|Parameter|Value|
|---|---|
|status|todo|
|status|in_progress|
|status|done|
|sort|created_at desc|

> Example responses

> 200 Response

```json
{
  "data": [
    {
      "id": "123e4567-e89b-12d3-a456-426614174002",
      "owner_user_id": "123e4567-e89b-12d3-a456-426614174000",
      "group_id": "123e4567-e89b-12d3-a456-426614174001",
      "title": "Prepare sprint handoff",
      "status": "in_progress",
      "visibility": "private",
      "created_at": "2026-02-10T09:00:00Z",
      "updated_at": "2026-02-10T10:30:00Z",
      "due_date": "2026-02-12",
      "start_time": "2026-02-10T09:00:00Z",
      "end_time": null,
      "tags": [
        "planning",
        "backend"
      ],
      "reference_links": [
        "https://docs.onlogn.com/specs/sprint-handoff"
      ]
    }
  ],
  "meta": {
    "pagination": {
      "offset": 0,
      "limit": 20,
      "total": 1,
      "sort": "created_at desc, id desc"
    }
  }
}
```

> RFC9457 problem details 응답.

```json
{
  "type": "https://api.onlogn.com/problems/authentication-required",
  "title": "Authentication Required",
  "status": 401,
  "detail": "Access token is missing or expired.",
  "instance": "/api/v1/users/me/req_01JZ4T3Q8R9A2F4M7N6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/permission-denied",
  "title": "Permission Denied",
  "status": 403,
  "detail": "You cannot access another user's private resource.",
  "instance": "/api/v1/groups/01ARZ3NDEKTSV4RRFFQ69G5FAV8/req_01JZ4T4H1X2B3C4D5E6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/resource-not-found",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "Requested resource does not exist.",
  "instance": "/api/v1/tasks/01ARZ3NDEKTSV4RRFFQ69G5FAV9/req_01JZ4T59K8L7M6N5P4Q"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/internal-server-error",
  "title": "Internal Server Error",
  "status": 500,
  "detail": "An unexpected error occurred while processing the request.",
  "instance": "/api/v1/groups/req_01JZ4T8V6W5X4Y3Z2A1",
  "request_id": "req_01JZ4T8V6W5X4Y3Z2A1"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/authentication-required",
  "title": "Authentication Required",
  "status": 401,
  "detail": "Access token is missing or expired.",
  "instance": "/api/v1/users/me/req_01JZ4T3Q8R9A2F4M7N6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/permission-denied",
  "title": "Permission Denied",
  "status": 403,
  "detail": "You cannot access another user's private resource.",
  "instance": "/api/v1/groups/01ARZ3NDEKTSV4RRFFQ69G5FAV8/req_01JZ4T4H1X2B3C4D5E6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/resource-not-found",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "Requested resource does not exist.",
  "instance": "/api/v1/tasks/01ARZ3NDEKTSV4RRFFQ69G5FAV9/req_01JZ4T59K8L7M6N5P4Q"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/internal-server-error",
  "title": "Internal Server Error",
  "status": 500,
  "detail": "An unexpected error occurred while processing the request.",
  "instance": "/api/v1/groups/req_01JZ4T8V6W5X4Y3Z2A1",
  "request_id": "req_01JZ4T8V6W5X4Y3Z2A1"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/authentication-required",
  "title": "Authentication Required",
  "status": 401,
  "detail": "Access token is missing or expired.",
  "instance": "/api/v1/users/me/req_01JZ4T3Q8R9A2F4M7N6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/permission-denied",
  "title": "Permission Denied",
  "status": 403,
  "detail": "You cannot access another user's private resource.",
  "instance": "/api/v1/groups/01ARZ3NDEKTSV4RRFFQ69G5FAV8/req_01JZ4T4H1X2B3C4D5E6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/resource-not-found",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "Requested resource does not exist.",
  "instance": "/api/v1/tasks/01ARZ3NDEKTSV4RRFFQ69G5FAV9/req_01JZ4T59K8L7M6N5P4Q"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/internal-server-error",
  "title": "Internal Server Error",
  "status": 500,
  "detail": "An unexpected error occurred while processing the request.",
  "instance": "/api/v1/groups/req_01JZ4T8V6W5X4Y3Z2A1",
  "request_id": "req_01JZ4T8V6W5X4Y3Z2A1"
}
```

<h3 id="listprofiletasks-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|profile의 공개 task 목록.|[TaskListResponse](#schematasklistresponse)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|RFC9457 problem details 응답.|[Problem](#schemaproblem)|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|RFC9457 problem details 응답.|[Problem](#schemaproblem)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|RFC9457 problem details 응답.|[Problem](#schemaproblem)|

<aside class="success">
This operation does not require authentication
</aside>

<h1 id="o-nlogn-api-v1-groups">groups</h1>

소유자 범위 group 관리 작업.

## listGroups

<a id="opIdlistGroups"></a>

> Code samples

```shell
# You can also use wget
curl -X GET https://api.onlogn.com/api/v1/groups \
  -H 'Accept: application/json' \
  -H 'Authorization: Bearer {access-token}'

```

```http
GET https://api.onlogn.com/api/v1/groups HTTP/1.1
Host: api.onlogn.com
Accept: application/json

```

```javascript

const headers = {
  'Accept':'application/json',
  'Authorization':'Bearer {access-token}'
};

fetch('https://api.onlogn.com/api/v1/groups',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

```ruby
require 'rest-client'
require 'json'

headers = {
  'Accept' => 'application/json',
  'Authorization' => 'Bearer {access-token}'
}

result = RestClient.get 'https://api.onlogn.com/api/v1/groups',
  params: {
  }, headers: headers

p JSON.parse(result)

```

```python
import requests
headers = {
  'Accept': 'application/json',
  'Authorization': 'Bearer {access-token}'
}

r = requests.get('https://api.onlogn.com/api/v1/groups', headers = headers)

print(r.json())

```

```php
<?php

require 'vendor/autoload.php';

$headers = array(
    'Accept' => 'application/json',
    'Authorization' => 'Bearer {access-token}',
);

$client = new \GuzzleHttp\Client();

// Define array of request body.
$request_body = array();

try {
    $response = $client->request('GET','https://api.onlogn.com/api/v1/groups', array(
        'headers' => $headers,
        'json' => $request_body,
       )
    );
    print_r($response->getBody()->getContents());
 }
 catch (\GuzzleHttp\Exception\BadResponseException $e) {
    // handle exception or api errors.
    print_r($e->getMessage());
 }

 // ...

```

```java
URL obj = new URL("https://api.onlogn.com/api/v1/groups");
HttpURLConnection con = (HttpURLConnection) obj.openConnection();
con.setRequestMethod("GET");
int responseCode = con.getResponseCode();
BufferedReader in = new BufferedReader(
    new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer response = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
}
in.close();
System.out.println(response.toString());

```

```go
package main

import (
       "bytes"
       "net/http"
)

func main() {

    headers := map[string][]string{
        "Accept": []string{"application/json"},
        "Authorization": []string{"Bearer {access-token}"},
    }

    data := bytes.NewBuffer([]byte{jsonReq})
    req, err := http.NewRequest("GET", "https://api.onlogn.com/api/v1/groups", data)
    req.Header = headers

    client := &http.Client{}
    resp, err := client.Do(req)
    // ...
}

```

`GET /api/v1/groups`

*group 목록 조회*

인증 사용자를 위한 owner 범위 group 목록.

<h3 id="listgroups-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|offset|query|integer|false|목록 페이지네이션용 0 기반 offset.|
|limit|query|integer|false|목록 페이지네이션용 페이지 크기.|

> Example responses

> 200 Response

```json
{
  "data": [
    {
      "id": "123e4567-e89b-12d3-a456-426614174001",
      "owner_user_id": "123e4567-e89b-12d3-a456-426614174000",
      "visibility": "private",
      "description": "Weekly planning workspace.",
      "color": "#4f46e5",
      "icon": "folder",
      "created_at": "2026-02-01T09:00:00Z",
      "updated_at": "2026-02-01T09:15:00Z"
    }
  ],
  "meta": {
    "pagination": {
      "offset": 0,
      "limit": 20,
      "total": 1,
      "sort": "created_at desc, id desc"
    }
  }
}
```

> RFC9457 problem details 응답.

```json
{
  "type": "https://api.onlogn.com/problems/authentication-required",
  "title": "Authentication Required",
  "status": 401,
  "detail": "Access token is missing or expired.",
  "instance": "/api/v1/users/me/req_01JZ4T3Q8R9A2F4M7N6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/permission-denied",
  "title": "Permission Denied",
  "status": 403,
  "detail": "You cannot access another user's private resource.",
  "instance": "/api/v1/groups/01ARZ3NDEKTSV4RRFFQ69G5FAV8/req_01JZ4T4H1X2B3C4D5E6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/resource-not-found",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "Requested resource does not exist.",
  "instance": "/api/v1/tasks/01ARZ3NDEKTSV4RRFFQ69G5FAV9/req_01JZ4T59K8L7M6N5P4Q"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/internal-server-error",
  "title": "Internal Server Error",
  "status": 500,
  "detail": "An unexpected error occurred while processing the request.",
  "instance": "/api/v1/groups/req_01JZ4T8V6W5X4Y3Z2A1",
  "request_id": "req_01JZ4T8V6W5X4Y3Z2A1"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/authentication-required",
  "title": "Authentication Required",
  "status": 401,
  "detail": "Access token is missing or expired.",
  "instance": "/api/v1/users/me/req_01JZ4T3Q8R9A2F4M7N6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/permission-denied",
  "title": "Permission Denied",
  "status": 403,
  "detail": "You cannot access another user's private resource.",
  "instance": "/api/v1/groups/01ARZ3NDEKTSV4RRFFQ69G5FAV8/req_01JZ4T4H1X2B3C4D5E6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/resource-not-found",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "Requested resource does not exist.",
  "instance": "/api/v1/tasks/01ARZ3NDEKTSV4RRFFQ69G5FAV9/req_01JZ4T59K8L7M6N5P4Q"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/internal-server-error",
  "title": "Internal Server Error",
  "status": 500,
  "detail": "An unexpected error occurred while processing the request.",
  "instance": "/api/v1/groups/req_01JZ4T8V6W5X4Y3Z2A1",
  "request_id": "req_01JZ4T8V6W5X4Y3Z2A1"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/authentication-required",
  "title": "Authentication Required",
  "status": 401,
  "detail": "Access token is missing or expired.",
  "instance": "/api/v1/users/me/req_01JZ4T3Q8R9A2F4M7N6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/permission-denied",
  "title": "Permission Denied",
  "status": 403,
  "detail": "You cannot access another user's private resource.",
  "instance": "/api/v1/groups/01ARZ3NDEKTSV4RRFFQ69G5FAV8/req_01JZ4T4H1X2B3C4D5E6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/resource-not-found",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "Requested resource does not exist.",
  "instance": "/api/v1/tasks/01ARZ3NDEKTSV4RRFFQ69G5FAV9/req_01JZ4T59K8L7M6N5P4Q"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/internal-server-error",
  "title": "Internal Server Error",
  "status": 500,
  "detail": "An unexpected error occurred while processing the request.",
  "instance": "/api/v1/groups/req_01JZ4T8V6W5X4Y3Z2A1",
  "request_id": "req_01JZ4T8V6W5X4Y3Z2A1"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/authentication-required",
  "title": "Authentication Required",
  "status": 401,
  "detail": "Access token is missing or expired.",
  "instance": "/api/v1/users/me/req_01JZ4T3Q8R9A2F4M7N6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/permission-denied",
  "title": "Permission Denied",
  "status": 403,
  "detail": "You cannot access another user's private resource.",
  "instance": "/api/v1/groups/01ARZ3NDEKTSV4RRFFQ69G5FAV8/req_01JZ4T4H1X2B3C4D5E6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/resource-not-found",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "Requested resource does not exist.",
  "instance": "/api/v1/tasks/01ARZ3NDEKTSV4RRFFQ69G5FAV9/req_01JZ4T59K8L7M6N5P4Q"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/internal-server-error",
  "title": "Internal Server Error",
  "status": 500,
  "detail": "An unexpected error occurred while processing the request.",
  "instance": "/api/v1/groups/req_01JZ4T8V6W5X4Y3Z2A1",
  "request_id": "req_01JZ4T8V6W5X4Y3Z2A1"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/authentication-required",
  "title": "Authentication Required",
  "status": 401,
  "detail": "Access token is missing or expired.",
  "instance": "/api/v1/users/me/req_01JZ4T3Q8R9A2F4M7N6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/permission-denied",
  "title": "Permission Denied",
  "status": 403,
  "detail": "You cannot access another user's private resource.",
  "instance": "/api/v1/groups/01ARZ3NDEKTSV4RRFFQ69G5FAV8/req_01JZ4T4H1X2B3C4D5E6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/resource-not-found",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "Requested resource does not exist.",
  "instance": "/api/v1/tasks/01ARZ3NDEKTSV4RRFFQ69G5FAV9/req_01JZ4T59K8L7M6N5P4Q"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/internal-server-error",
  "title": "Internal Server Error",
  "status": 500,
  "detail": "An unexpected error occurred while processing the request.",
  "instance": "/api/v1/groups/req_01JZ4T8V6W5X4Y3Z2A1",
  "request_id": "req_01JZ4T8V6W5X4Y3Z2A1"
}
```

<h3 id="listgroups-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|Group 목록.|[GroupListResponse](#schemagrouplistresponse)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|RFC9457 problem details 응답.|[Problem](#schemaproblem)|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|RFC9457 problem details 응답.|[Problem](#schemaproblem)|
|403|[Forbidden](https://tools.ietf.org/html/rfc7231#section-6.5.3)|RFC9457 problem details 응답.|[Problem](#schemaproblem)|
|429|[Too Many Requests](https://tools.ietf.org/html/rfc6585#section-4)|RFC9457 problem details 응답.|[Problem](#schemaproblem)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|RFC9457 problem details 응답.|[Problem](#schemaproblem)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## createGroup

<a id="opIdcreateGroup"></a>

> Code samples

```shell
# You can also use wget
curl -X POST https://api.onlogn.com/api/v1/groups \
  -H 'Content-Type: application/json' \
  -H 'Accept: application/json' \
  -H 'Authorization: Bearer {access-token}'

```

```http
POST https://api.onlogn.com/api/v1/groups HTTP/1.1
Host: api.onlogn.com
Content-Type: application/json
Accept: application/json

```

```javascript
const inputBody = '{
  "visibility": "private",
  "description": "New group for high priority work.",
  "color": "#f97316",
  "icon": "star"
}';
const headers = {
  'Content-Type':'application/json',
  'Accept':'application/json',
  'Authorization':'Bearer {access-token}'
};

fetch('https://api.onlogn.com/api/v1/groups',
{
  method: 'POST',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

```ruby
require 'rest-client'
require 'json'

headers = {
  'Content-Type' => 'application/json',
  'Accept' => 'application/json',
  'Authorization' => 'Bearer {access-token}'
}

result = RestClient.post 'https://api.onlogn.com/api/v1/groups',
  params: {
  }, headers: headers

p JSON.parse(result)

```

```python
import requests
headers = {
  'Content-Type': 'application/json',
  'Accept': 'application/json',
  'Authorization': 'Bearer {access-token}'
}

r = requests.post('https://api.onlogn.com/api/v1/groups', headers = headers)

print(r.json())

```

```php
<?php

require 'vendor/autoload.php';

$headers = array(
    'Content-Type' => 'application/json',
    'Accept' => 'application/json',
    'Authorization' => 'Bearer {access-token}',
);

$client = new \GuzzleHttp\Client();

// Define array of request body.
$request_body = array();

try {
    $response = $client->request('POST','https://api.onlogn.com/api/v1/groups', array(
        'headers' => $headers,
        'json' => $request_body,
       )
    );
    print_r($response->getBody()->getContents());
 }
 catch (\GuzzleHttp\Exception\BadResponseException $e) {
    // handle exception or api errors.
    print_r($e->getMessage());
 }

 // ...

```

```java
URL obj = new URL("https://api.onlogn.com/api/v1/groups");
HttpURLConnection con = (HttpURLConnection) obj.openConnection();
con.setRequestMethod("POST");
int responseCode = con.getResponseCode();
BufferedReader in = new BufferedReader(
    new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer response = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
}
in.close();
System.out.println(response.toString());

```

```go
package main

import (
       "bytes"
       "net/http"
)

func main() {

    headers := map[string][]string{
        "Content-Type": []string{"application/json"},
        "Accept": []string{"application/json"},
        "Authorization": []string{"Bearer {access-token}"},
    }

    data := bytes.NewBuffer([]byte{jsonReq})
    req, err := http.NewRequest("POST", "https://api.onlogn.com/api/v1/groups", data)
    req.Header = headers

    client := &http.Client{}
    resp, err := client.Do(req)
    // ...
}

```

`POST /api/v1/groups`

*group 생성*

새 owner 범위 group을 생성한다.

> Body parameter

```json
{
  "visibility": "private",
  "description": "New group for high priority work.",
  "color": "#f97316",
  "icon": "star"
}
```

<h3 id="creategroup-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|body|body|[GroupCreateRequest](#schemagroupcreaterequest)|true|none|

> Example responses

> 201 Response

```json
{
  "data": {
    "id": "123e4567-e89b-12d3-a456-426614174001",
    "owner_user_id": "123e4567-e89b-12d3-a456-426614174000",
    "visibility": "private",
    "description": "Weekly planning workspace.",
    "color": "#4f46e5",
    "icon": "folder",
    "created_at": "2026-02-01T09:00:00Z",
    "updated_at": "2026-02-01T09:15:00Z"
  }
}
```

> RFC9457 problem details 응답.

```json
{
  "type": "https://api.onlogn.com/problems/authentication-required",
  "title": "Authentication Required",
  "status": 401,
  "detail": "Access token is missing or expired.",
  "instance": "/api/v1/users/me/req_01JZ4T3Q8R9A2F4M7N6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/permission-denied",
  "title": "Permission Denied",
  "status": 403,
  "detail": "You cannot access another user's private resource.",
  "instance": "/api/v1/groups/01ARZ3NDEKTSV4RRFFQ69G5FAV8/req_01JZ4T4H1X2B3C4D5E6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/resource-not-found",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "Requested resource does not exist.",
  "instance": "/api/v1/tasks/01ARZ3NDEKTSV4RRFFQ69G5FAV9/req_01JZ4T59K8L7M6N5P4Q"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/internal-server-error",
  "title": "Internal Server Error",
  "status": 500,
  "detail": "An unexpected error occurred while processing the request.",
  "instance": "/api/v1/groups/req_01JZ4T8V6W5X4Y3Z2A1",
  "request_id": "req_01JZ4T8V6W5X4Y3Z2A1"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/authentication-required",
  "title": "Authentication Required",
  "status": 401,
  "detail": "Access token is missing or expired.",
  "instance": "/api/v1/users/me/req_01JZ4T3Q8R9A2F4M7N6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/permission-denied",
  "title": "Permission Denied",
  "status": 403,
  "detail": "You cannot access another user's private resource.",
  "instance": "/api/v1/groups/01ARZ3NDEKTSV4RRFFQ69G5FAV8/req_01JZ4T4H1X2B3C4D5E6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/resource-not-found",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "Requested resource does not exist.",
  "instance": "/api/v1/tasks/01ARZ3NDEKTSV4RRFFQ69G5FAV9/req_01JZ4T59K8L7M6N5P4Q"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/internal-server-error",
  "title": "Internal Server Error",
  "status": 500,
  "detail": "An unexpected error occurred while processing the request.",
  "instance": "/api/v1/groups/req_01JZ4T8V6W5X4Y3Z2A1",
  "request_id": "req_01JZ4T8V6W5X4Y3Z2A1"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/authentication-required",
  "title": "Authentication Required",
  "status": 401,
  "detail": "Access token is missing or expired.",
  "instance": "/api/v1/users/me/req_01JZ4T3Q8R9A2F4M7N6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/permission-denied",
  "title": "Permission Denied",
  "status": 403,
  "detail": "You cannot access another user's private resource.",
  "instance": "/api/v1/groups/01ARZ3NDEKTSV4RRFFQ69G5FAV8/req_01JZ4T4H1X2B3C4D5E6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/resource-not-found",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "Requested resource does not exist.",
  "instance": "/api/v1/tasks/01ARZ3NDEKTSV4RRFFQ69G5FAV9/req_01JZ4T59K8L7M6N5P4Q"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/internal-server-error",
  "title": "Internal Server Error",
  "status": 500,
  "detail": "An unexpected error occurred while processing the request.",
  "instance": "/api/v1/groups/req_01JZ4T8V6W5X4Y3Z2A1",
  "request_id": "req_01JZ4T8V6W5X4Y3Z2A1"
}
```

> 422 Response

```json
{
  "type": "https://api.onlogn.com/problems/validation-failed",
  "title": "Validation Failed",
  "status": 422,
  "detail": "Request body validation failed.",
  "instance": "/api/v1/tasks/req_01JZ4T59K8L7M6N5P4Q",
  "errors": [
    {
      "field": "status",
      "reason": "must be one of: todo, in_progress, done"
    },
    {
      "field": "due_date",
      "reason": "must be a valid date in YYYY-MM-DD format"
    }
  ]
}
```

> RFC9457 problem details 응답.

```json
{
  "type": "https://api.onlogn.com/problems/authentication-required",
  "title": "Authentication Required",
  "status": 401,
  "detail": "Access token is missing or expired.",
  "instance": "/api/v1/users/me/req_01JZ4T3Q8R9A2F4M7N6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/permission-denied",
  "title": "Permission Denied",
  "status": 403,
  "detail": "You cannot access another user's private resource.",
  "instance": "/api/v1/groups/01ARZ3NDEKTSV4RRFFQ69G5FAV8/req_01JZ4T4H1X2B3C4D5E6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/resource-not-found",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "Requested resource does not exist.",
  "instance": "/api/v1/tasks/01ARZ3NDEKTSV4RRFFQ69G5FAV9/req_01JZ4T59K8L7M6N5P4Q"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/internal-server-error",
  "title": "Internal Server Error",
  "status": 500,
  "detail": "An unexpected error occurred while processing the request.",
  "instance": "/api/v1/groups/req_01JZ4T8V6W5X4Y3Z2A1",
  "request_id": "req_01JZ4T8V6W5X4Y3Z2A1"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/authentication-required",
  "title": "Authentication Required",
  "status": 401,
  "detail": "Access token is missing or expired.",
  "instance": "/api/v1/users/me/req_01JZ4T3Q8R9A2F4M7N6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/permission-denied",
  "title": "Permission Denied",
  "status": 403,
  "detail": "You cannot access another user's private resource.",
  "instance": "/api/v1/groups/01ARZ3NDEKTSV4RRFFQ69G5FAV8/req_01JZ4T4H1X2B3C4D5E6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/resource-not-found",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "Requested resource does not exist.",
  "instance": "/api/v1/tasks/01ARZ3NDEKTSV4RRFFQ69G5FAV9/req_01JZ4T59K8L7M6N5P4Q"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/internal-server-error",
  "title": "Internal Server Error",
  "status": 500,
  "detail": "An unexpected error occurred while processing the request.",
  "instance": "/api/v1/groups/req_01JZ4T8V6W5X4Y3Z2A1",
  "request_id": "req_01JZ4T8V6W5X4Y3Z2A1"
}
```

<h3 id="creategroup-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|201|[Created](https://tools.ietf.org/html/rfc7231#section-6.3.2)|Group 생성 완료.|[GroupResponse](#schemagroupresponse)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|RFC9457 problem details 응답.|[Problem](#schemaproblem)|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|RFC9457 problem details 응답.|[Problem](#schemaproblem)|
|403|[Forbidden](https://tools.ietf.org/html/rfc7231#section-6.5.3)|RFC9457 problem details 응답.|[Problem](#schemaproblem)|
|422|[Unprocessable Entity](https://tools.ietf.org/html/rfc2518#section-10.3)|RFC9457 validation problem details 응답.|[ValidationProblem](#schemavalidationproblem)|
|429|[Too Many Requests](https://tools.ietf.org/html/rfc6585#section-4)|RFC9457 problem details 응답.|[Problem](#schemaproblem)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|RFC9457 problem details 응답.|[Problem](#schemaproblem)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## getGroup

<a id="opIdgetGroup"></a>

> Code samples

```shell
# You can also use wget
curl -X GET https://api.onlogn.com/api/v1/groups/{group_id} \
  -H 'Accept: application/json' \
  -H 'Authorization: Bearer {access-token}'

```

```http
GET https://api.onlogn.com/api/v1/groups/{group_id} HTTP/1.1
Host: api.onlogn.com
Accept: application/json

```

```javascript

const headers = {
  'Accept':'application/json',
  'Authorization':'Bearer {access-token}'
};

fetch('https://api.onlogn.com/api/v1/groups/{group_id}',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

```ruby
require 'rest-client'
require 'json'

headers = {
  'Accept' => 'application/json',
  'Authorization' => 'Bearer {access-token}'
}

result = RestClient.get 'https://api.onlogn.com/api/v1/groups/{group_id}',
  params: {
  }, headers: headers

p JSON.parse(result)

```

```python
import requests
headers = {
  'Accept': 'application/json',
  'Authorization': 'Bearer {access-token}'
}

r = requests.get('https://api.onlogn.com/api/v1/groups/{group_id}', headers = headers)

print(r.json())

```

```php
<?php

require 'vendor/autoload.php';

$headers = array(
    'Accept' => 'application/json',
    'Authorization' => 'Bearer {access-token}',
);

$client = new \GuzzleHttp\Client();

// Define array of request body.
$request_body = array();

try {
    $response = $client->request('GET','https://api.onlogn.com/api/v1/groups/{group_id}', array(
        'headers' => $headers,
        'json' => $request_body,
       )
    );
    print_r($response->getBody()->getContents());
 }
 catch (\GuzzleHttp\Exception\BadResponseException $e) {
    // handle exception or api errors.
    print_r($e->getMessage());
 }

 // ...

```

```java
URL obj = new URL("https://api.onlogn.com/api/v1/groups/{group_id}");
HttpURLConnection con = (HttpURLConnection) obj.openConnection();
con.setRequestMethod("GET");
int responseCode = con.getResponseCode();
BufferedReader in = new BufferedReader(
    new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer response = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
}
in.close();
System.out.println(response.toString());

```

```go
package main

import (
       "bytes"
       "net/http"
)

func main() {

    headers := map[string][]string{
        "Accept": []string{"application/json"},
        "Authorization": []string{"Bearer {access-token}"},
    }

    data := bytes.NewBuffer([]byte{jsonReq})
    req, err := http.NewRequest("GET", "https://api.onlogn.com/api/v1/groups/{group_id}", data)
    req.Header = headers

    client := &http.Client{}
    resp, err := client.Do(req)
    // ...
}

```

`GET /api/v1/groups/{group_id}`

*group 조회*

owner 범위에서 단일 group을 조회한다.

<h3 id="getgroup-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|group_id|path|string(uuid)|true|none|

> Example responses

> 200 Response

```json
{
  "data": {
    "id": "123e4567-e89b-12d3-a456-426614174001",
    "owner_user_id": "123e4567-e89b-12d3-a456-426614174000",
    "visibility": "private",
    "description": "Weekly planning workspace.",
    "color": "#4f46e5",
    "icon": "folder",
    "created_at": "2026-02-01T09:00:00Z",
    "updated_at": "2026-02-01T09:15:00Z"
  }
}
```

> RFC9457 problem details 응답.

```json
{
  "type": "https://api.onlogn.com/problems/authentication-required",
  "title": "Authentication Required",
  "status": 401,
  "detail": "Access token is missing or expired.",
  "instance": "/api/v1/users/me/req_01JZ4T3Q8R9A2F4M7N6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/permission-denied",
  "title": "Permission Denied",
  "status": 403,
  "detail": "You cannot access another user's private resource.",
  "instance": "/api/v1/groups/01ARZ3NDEKTSV4RRFFQ69G5FAV8/req_01JZ4T4H1X2B3C4D5E6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/resource-not-found",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "Requested resource does not exist.",
  "instance": "/api/v1/tasks/01ARZ3NDEKTSV4RRFFQ69G5FAV9/req_01JZ4T59K8L7M6N5P4Q"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/internal-server-error",
  "title": "Internal Server Error",
  "status": 500,
  "detail": "An unexpected error occurred while processing the request.",
  "instance": "/api/v1/groups/req_01JZ4T8V6W5X4Y3Z2A1",
  "request_id": "req_01JZ4T8V6W5X4Y3Z2A1"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/authentication-required",
  "title": "Authentication Required",
  "status": 401,
  "detail": "Access token is missing or expired.",
  "instance": "/api/v1/users/me/req_01JZ4T3Q8R9A2F4M7N6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/permission-denied",
  "title": "Permission Denied",
  "status": 403,
  "detail": "You cannot access another user's private resource.",
  "instance": "/api/v1/groups/01ARZ3NDEKTSV4RRFFQ69G5FAV8/req_01JZ4T4H1X2B3C4D5E6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/resource-not-found",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "Requested resource does not exist.",
  "instance": "/api/v1/tasks/01ARZ3NDEKTSV4RRFFQ69G5FAV9/req_01JZ4T59K8L7M6N5P4Q"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/internal-server-error",
  "title": "Internal Server Error",
  "status": 500,
  "detail": "An unexpected error occurred while processing the request.",
  "instance": "/api/v1/groups/req_01JZ4T8V6W5X4Y3Z2A1",
  "request_id": "req_01JZ4T8V6W5X4Y3Z2A1"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/authentication-required",
  "title": "Authentication Required",
  "status": 401,
  "detail": "Access token is missing or expired.",
  "instance": "/api/v1/users/me/req_01JZ4T3Q8R9A2F4M7N6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/permission-denied",
  "title": "Permission Denied",
  "status": 403,
  "detail": "You cannot access another user's private resource.",
  "instance": "/api/v1/groups/01ARZ3NDEKTSV4RRFFQ69G5FAV8/req_01JZ4T4H1X2B3C4D5E6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/resource-not-found",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "Requested resource does not exist.",
  "instance": "/api/v1/tasks/01ARZ3NDEKTSV4RRFFQ69G5FAV9/req_01JZ4T59K8L7M6N5P4Q"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/internal-server-error",
  "title": "Internal Server Error",
  "status": 500,
  "detail": "An unexpected error occurred while processing the request.",
  "instance": "/api/v1/groups/req_01JZ4T8V6W5X4Y3Z2A1",
  "request_id": "req_01JZ4T8V6W5X4Y3Z2A1"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/authentication-required",
  "title": "Authentication Required",
  "status": 401,
  "detail": "Access token is missing or expired.",
  "instance": "/api/v1/users/me/req_01JZ4T3Q8R9A2F4M7N6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/permission-denied",
  "title": "Permission Denied",
  "status": 403,
  "detail": "You cannot access another user's private resource.",
  "instance": "/api/v1/groups/01ARZ3NDEKTSV4RRFFQ69G5FAV8/req_01JZ4T4H1X2B3C4D5E6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/resource-not-found",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "Requested resource does not exist.",
  "instance": "/api/v1/tasks/01ARZ3NDEKTSV4RRFFQ69G5FAV9/req_01JZ4T59K8L7M6N5P4Q"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/internal-server-error",
  "title": "Internal Server Error",
  "status": 500,
  "detail": "An unexpected error occurred while processing the request.",
  "instance": "/api/v1/groups/req_01JZ4T8V6W5X4Y3Z2A1",
  "request_id": "req_01JZ4T8V6W5X4Y3Z2A1"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/authentication-required",
  "title": "Authentication Required",
  "status": 401,
  "detail": "Access token is missing or expired.",
  "instance": "/api/v1/users/me/req_01JZ4T3Q8R9A2F4M7N6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/permission-denied",
  "title": "Permission Denied",
  "status": 403,
  "detail": "You cannot access another user's private resource.",
  "instance": "/api/v1/groups/01ARZ3NDEKTSV4RRFFQ69G5FAV8/req_01JZ4T4H1X2B3C4D5E6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/resource-not-found",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "Requested resource does not exist.",
  "instance": "/api/v1/tasks/01ARZ3NDEKTSV4RRFFQ69G5FAV9/req_01JZ4T59K8L7M6N5P4Q"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/internal-server-error",
  "title": "Internal Server Error",
  "status": 500,
  "detail": "An unexpected error occurred while processing the request.",
  "instance": "/api/v1/groups/req_01JZ4T8V6W5X4Y3Z2A1",
  "request_id": "req_01JZ4T8V6W5X4Y3Z2A1"
}
```

<h3 id="getgroup-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|Group 상세.|[GroupResponse](#schemagroupresponse)|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|RFC9457 problem details 응답.|[Problem](#schemaproblem)|
|403|[Forbidden](https://tools.ietf.org/html/rfc7231#section-6.5.3)|RFC9457 problem details 응답.|[Problem](#schemaproblem)|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|RFC9457 problem details 응답.|[Problem](#schemaproblem)|
|429|[Too Many Requests](https://tools.ietf.org/html/rfc6585#section-4)|RFC9457 problem details 응답.|[Problem](#schemaproblem)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|RFC9457 problem details 응답.|[Problem](#schemaproblem)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## updateGroup

<a id="opIdupdateGroup"></a>

> Code samples

```shell
# You can also use wget
curl -X PATCH https://api.onlogn.com/api/v1/groups/{group_id} \
  -H 'Content-Type: application/json' \
  -H 'Accept: application/json' \
  -H 'Authorization: Bearer {access-token}'

```

```http
PATCH https://api.onlogn.com/api/v1/groups/{group_id} HTTP/1.1
Host: api.onlogn.com
Content-Type: application/json
Accept: application/json

```

```javascript
const inputBody = '{
  "visibility": "private",
  "description": "Updated notes for this group.",
  "color": "#0ea5e9",
  "icon": "tag"
}';
const headers = {
  'Content-Type':'application/json',
  'Accept':'application/json',
  'Authorization':'Bearer {access-token}'
};

fetch('https://api.onlogn.com/api/v1/groups/{group_id}',
{
  method: 'PATCH',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

```ruby
require 'rest-client'
require 'json'

headers = {
  'Content-Type' => 'application/json',
  'Accept' => 'application/json',
  'Authorization' => 'Bearer {access-token}'
}

result = RestClient.patch 'https://api.onlogn.com/api/v1/groups/{group_id}',
  params: {
  }, headers: headers

p JSON.parse(result)

```

```python
import requests
headers = {
  'Content-Type': 'application/json',
  'Accept': 'application/json',
  'Authorization': 'Bearer {access-token}'
}

r = requests.patch('https://api.onlogn.com/api/v1/groups/{group_id}', headers = headers)

print(r.json())

```

```php
<?php

require 'vendor/autoload.php';

$headers = array(
    'Content-Type' => 'application/json',
    'Accept' => 'application/json',
    'Authorization' => 'Bearer {access-token}',
);

$client = new \GuzzleHttp\Client();

// Define array of request body.
$request_body = array();

try {
    $response = $client->request('PATCH','https://api.onlogn.com/api/v1/groups/{group_id}', array(
        'headers' => $headers,
        'json' => $request_body,
       )
    );
    print_r($response->getBody()->getContents());
 }
 catch (\GuzzleHttp\Exception\BadResponseException $e) {
    // handle exception or api errors.
    print_r($e->getMessage());
 }

 // ...

```

```java
URL obj = new URL("https://api.onlogn.com/api/v1/groups/{group_id}");
HttpURLConnection con = (HttpURLConnection) obj.openConnection();
con.setRequestMethod("PATCH");
int responseCode = con.getResponseCode();
BufferedReader in = new BufferedReader(
    new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer response = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
}
in.close();
System.out.println(response.toString());

```

```go
package main

import (
       "bytes"
       "net/http"
)

func main() {

    headers := map[string][]string{
        "Content-Type": []string{"application/json"},
        "Accept": []string{"application/json"},
        "Authorization": []string{"Bearer {access-token}"},
    }

    data := bytes.NewBuffer([]byte{jsonReq})
    req, err := http.NewRequest("PATCH", "https://api.onlogn.com/api/v1/groups/{group_id}", data)
    req.Header = headers

    client := &http.Client{}
    resp, err := client.Do(req)
    // ...
}

```

`PATCH /api/v1/groups/{group_id}`

*group 수정*

owner 범위 group 메타데이터(visibility, description, color, icon)를 수정한다.

> Body parameter

```json
{
  "visibility": "private",
  "description": "Updated notes for this group.",
  "color": "#0ea5e9",
  "icon": "tag"
}
```

<h3 id="updategroup-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|group_id|path|string(uuid)|true|none|
|body|body|[GroupUpdateRequest](#schemagroupupdaterequest)|true|none|

> Example responses

> 200 Response

```json
{
  "data": {
    "id": "123e4567-e89b-12d3-a456-426614174001",
    "owner_user_id": "123e4567-e89b-12d3-a456-426614174000",
    "visibility": "private",
    "description": "Weekly planning workspace.",
    "color": "#4f46e5",
    "icon": "folder",
    "created_at": "2026-02-01T09:00:00Z",
    "updated_at": "2026-02-01T09:15:00Z"
  }
}
```

> RFC9457 problem details 응답.

```json
{
  "type": "https://api.onlogn.com/problems/authentication-required",
  "title": "Authentication Required",
  "status": 401,
  "detail": "Access token is missing or expired.",
  "instance": "/api/v1/users/me/req_01JZ4T3Q8R9A2F4M7N6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/permission-denied",
  "title": "Permission Denied",
  "status": 403,
  "detail": "You cannot access another user's private resource.",
  "instance": "/api/v1/groups/01ARZ3NDEKTSV4RRFFQ69G5FAV8/req_01JZ4T4H1X2B3C4D5E6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/resource-not-found",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "Requested resource does not exist.",
  "instance": "/api/v1/tasks/01ARZ3NDEKTSV4RRFFQ69G5FAV9/req_01JZ4T59K8L7M6N5P4Q"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/internal-server-error",
  "title": "Internal Server Error",
  "status": 500,
  "detail": "An unexpected error occurred while processing the request.",
  "instance": "/api/v1/groups/req_01JZ4T8V6W5X4Y3Z2A1",
  "request_id": "req_01JZ4T8V6W5X4Y3Z2A1"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/authentication-required",
  "title": "Authentication Required",
  "status": 401,
  "detail": "Access token is missing or expired.",
  "instance": "/api/v1/users/me/req_01JZ4T3Q8R9A2F4M7N6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/permission-denied",
  "title": "Permission Denied",
  "status": 403,
  "detail": "You cannot access another user's private resource.",
  "instance": "/api/v1/groups/01ARZ3NDEKTSV4RRFFQ69G5FAV8/req_01JZ4T4H1X2B3C4D5E6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/resource-not-found",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "Requested resource does not exist.",
  "instance": "/api/v1/tasks/01ARZ3NDEKTSV4RRFFQ69G5FAV9/req_01JZ4T59K8L7M6N5P4Q"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/internal-server-error",
  "title": "Internal Server Error",
  "status": 500,
  "detail": "An unexpected error occurred while processing the request.",
  "instance": "/api/v1/groups/req_01JZ4T8V6W5X4Y3Z2A1",
  "request_id": "req_01JZ4T8V6W5X4Y3Z2A1"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/authentication-required",
  "title": "Authentication Required",
  "status": 401,
  "detail": "Access token is missing or expired.",
  "instance": "/api/v1/users/me/req_01JZ4T3Q8R9A2F4M7N6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/permission-denied",
  "title": "Permission Denied",
  "status": 403,
  "detail": "You cannot access another user's private resource.",
  "instance": "/api/v1/groups/01ARZ3NDEKTSV4RRFFQ69G5FAV8/req_01JZ4T4H1X2B3C4D5E6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/resource-not-found",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "Requested resource does not exist.",
  "instance": "/api/v1/tasks/01ARZ3NDEKTSV4RRFFQ69G5FAV9/req_01JZ4T59K8L7M6N5P4Q"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/internal-server-error",
  "title": "Internal Server Error",
  "status": 500,
  "detail": "An unexpected error occurred while processing the request.",
  "instance": "/api/v1/groups/req_01JZ4T8V6W5X4Y3Z2A1",
  "request_id": "req_01JZ4T8V6W5X4Y3Z2A1"
}
```

> 422 Response

```json
{
  "type": "https://api.onlogn.com/problems/validation-failed",
  "title": "Validation Failed",
  "status": 422,
  "detail": "Request body validation failed.",
  "instance": "/api/v1/tasks/req_01JZ4T59K8L7M6N5P4Q",
  "errors": [
    {
      "field": "status",
      "reason": "must be one of: todo, in_progress, done"
    },
    {
      "field": "due_date",
      "reason": "must be a valid date in YYYY-MM-DD format"
    }
  ]
}
```

> RFC9457 problem details 응답.

```json
{
  "type": "https://api.onlogn.com/problems/authentication-required",
  "title": "Authentication Required",
  "status": 401,
  "detail": "Access token is missing or expired.",
  "instance": "/api/v1/users/me/req_01JZ4T3Q8R9A2F4M7N6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/permission-denied",
  "title": "Permission Denied",
  "status": 403,
  "detail": "You cannot access another user's private resource.",
  "instance": "/api/v1/groups/01ARZ3NDEKTSV4RRFFQ69G5FAV8/req_01JZ4T4H1X2B3C4D5E6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/resource-not-found",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "Requested resource does not exist.",
  "instance": "/api/v1/tasks/01ARZ3NDEKTSV4RRFFQ69G5FAV9/req_01JZ4T59K8L7M6N5P4Q"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/internal-server-error",
  "title": "Internal Server Error",
  "status": 500,
  "detail": "An unexpected error occurred while processing the request.",
  "instance": "/api/v1/groups/req_01JZ4T8V6W5X4Y3Z2A1",
  "request_id": "req_01JZ4T8V6W5X4Y3Z2A1"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/authentication-required",
  "title": "Authentication Required",
  "status": 401,
  "detail": "Access token is missing or expired.",
  "instance": "/api/v1/users/me/req_01JZ4T3Q8R9A2F4M7N6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/permission-denied",
  "title": "Permission Denied",
  "status": 403,
  "detail": "You cannot access another user's private resource.",
  "instance": "/api/v1/groups/01ARZ3NDEKTSV4RRFFQ69G5FAV8/req_01JZ4T4H1X2B3C4D5E6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/resource-not-found",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "Requested resource does not exist.",
  "instance": "/api/v1/tasks/01ARZ3NDEKTSV4RRFFQ69G5FAV9/req_01JZ4T59K8L7M6N5P4Q"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/internal-server-error",
  "title": "Internal Server Error",
  "status": 500,
  "detail": "An unexpected error occurred while processing the request.",
  "instance": "/api/v1/groups/req_01JZ4T8V6W5X4Y3Z2A1",
  "request_id": "req_01JZ4T8V6W5X4Y3Z2A1"
}
```

<h3 id="updategroup-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|Group 수정 완료.|[GroupResponse](#schemagroupresponse)|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|RFC9457 problem details 응답.|[Problem](#schemaproblem)|
|403|[Forbidden](https://tools.ietf.org/html/rfc7231#section-6.5.3)|RFC9457 problem details 응답.|[Problem](#schemaproblem)|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|RFC9457 problem details 응답.|[Problem](#schemaproblem)|
|422|[Unprocessable Entity](https://tools.ietf.org/html/rfc2518#section-10.3)|RFC9457 validation problem details 응답.|[ValidationProblem](#schemavalidationproblem)|
|429|[Too Many Requests](https://tools.ietf.org/html/rfc6585#section-4)|RFC9457 problem details 응답.|[Problem](#schemaproblem)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|RFC9457 problem details 응답.|[Problem](#schemaproblem)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## deleteGroup

<a id="opIddeleteGroup"></a>

> Code samples

```shell
# You can also use wget
curl -X DELETE https://api.onlogn.com/api/v1/groups/{group_id} \
  -H 'Accept: application/problem+json' \
  -H 'Authorization: Bearer {access-token}'

```

```http
DELETE https://api.onlogn.com/api/v1/groups/{group_id} HTTP/1.1
Host: api.onlogn.com
Accept: application/problem+json

```

```javascript

const headers = {
  'Accept':'application/problem+json',
  'Authorization':'Bearer {access-token}'
};

fetch('https://api.onlogn.com/api/v1/groups/{group_id}',
{
  method: 'DELETE',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

```ruby
require 'rest-client'
require 'json'

headers = {
  'Accept' => 'application/problem+json',
  'Authorization' => 'Bearer {access-token}'
}

result = RestClient.delete 'https://api.onlogn.com/api/v1/groups/{group_id}',
  params: {
  }, headers: headers

p JSON.parse(result)

```

```python
import requests
headers = {
  'Accept': 'application/problem+json',
  'Authorization': 'Bearer {access-token}'
}

r = requests.delete('https://api.onlogn.com/api/v1/groups/{group_id}', headers = headers)

print(r.json())

```

```php
<?php

require 'vendor/autoload.php';

$headers = array(
    'Accept' => 'application/problem+json',
    'Authorization' => 'Bearer {access-token}',
);

$client = new \GuzzleHttp\Client();

// Define array of request body.
$request_body = array();

try {
    $response = $client->request('DELETE','https://api.onlogn.com/api/v1/groups/{group_id}', array(
        'headers' => $headers,
        'json' => $request_body,
       )
    );
    print_r($response->getBody()->getContents());
 }
 catch (\GuzzleHttp\Exception\BadResponseException $e) {
    // handle exception or api errors.
    print_r($e->getMessage());
 }

 // ...

```

```java
URL obj = new URL("https://api.onlogn.com/api/v1/groups/{group_id}");
HttpURLConnection con = (HttpURLConnection) obj.openConnection();
con.setRequestMethod("DELETE");
int responseCode = con.getResponseCode();
BufferedReader in = new BufferedReader(
    new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer response = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
}
in.close();
System.out.println(response.toString());

```

```go
package main

import (
       "bytes"
       "net/http"
)

func main() {

    headers := map[string][]string{
        "Accept": []string{"application/problem+json"},
        "Authorization": []string{"Bearer {access-token}"},
    }

    data := bytes.NewBuffer([]byte{jsonReq})
    req, err := http.NewRequest("DELETE", "https://api.onlogn.com/api/v1/groups/{group_id}", data)
    req.Header = headers

    client := &http.Client{}
    resp, err := client.Do(req)
    // ...
}

```

`DELETE /api/v1/groups/{group_id}`

*group 삭제*

소유한 group을 삭제한다. 연결된 task는 `group_id = null`로 재할당된다.

<h3 id="deletegroup-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|group_id|path|string(uuid)|true|none|

> Example responses

> RFC9457 problem details 응답.

```json
{
  "type": "https://api.onlogn.com/problems/authentication-required",
  "title": "Authentication Required",
  "status": 401,
  "detail": "Access token is missing or expired.",
  "instance": "/api/v1/users/me/req_01JZ4T3Q8R9A2F4M7N6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/permission-denied",
  "title": "Permission Denied",
  "status": 403,
  "detail": "You cannot access another user's private resource.",
  "instance": "/api/v1/groups/01ARZ3NDEKTSV4RRFFQ69G5FAV8/req_01JZ4T4H1X2B3C4D5E6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/resource-not-found",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "Requested resource does not exist.",
  "instance": "/api/v1/tasks/01ARZ3NDEKTSV4RRFFQ69G5FAV9/req_01JZ4T59K8L7M6N5P4Q"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/internal-server-error",
  "title": "Internal Server Error",
  "status": 500,
  "detail": "An unexpected error occurred while processing the request.",
  "instance": "/api/v1/groups/req_01JZ4T8V6W5X4Y3Z2A1",
  "request_id": "req_01JZ4T8V6W5X4Y3Z2A1"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/authentication-required",
  "title": "Authentication Required",
  "status": 401,
  "detail": "Access token is missing or expired.",
  "instance": "/api/v1/users/me/req_01JZ4T3Q8R9A2F4M7N6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/permission-denied",
  "title": "Permission Denied",
  "status": 403,
  "detail": "You cannot access another user's private resource.",
  "instance": "/api/v1/groups/01ARZ3NDEKTSV4RRFFQ69G5FAV8/req_01JZ4T4H1X2B3C4D5E6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/resource-not-found",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "Requested resource does not exist.",
  "instance": "/api/v1/tasks/01ARZ3NDEKTSV4RRFFQ69G5FAV9/req_01JZ4T59K8L7M6N5P4Q"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/internal-server-error",
  "title": "Internal Server Error",
  "status": 500,
  "detail": "An unexpected error occurred while processing the request.",
  "instance": "/api/v1/groups/req_01JZ4T8V6W5X4Y3Z2A1",
  "request_id": "req_01JZ4T8V6W5X4Y3Z2A1"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/authentication-required",
  "title": "Authentication Required",
  "status": 401,
  "detail": "Access token is missing or expired.",
  "instance": "/api/v1/users/me/req_01JZ4T3Q8R9A2F4M7N6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/permission-denied",
  "title": "Permission Denied",
  "status": 403,
  "detail": "You cannot access another user's private resource.",
  "instance": "/api/v1/groups/01ARZ3NDEKTSV4RRFFQ69G5FAV8/req_01JZ4T4H1X2B3C4D5E6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/resource-not-found",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "Requested resource does not exist.",
  "instance": "/api/v1/tasks/01ARZ3NDEKTSV4RRFFQ69G5FAV9/req_01JZ4T59K8L7M6N5P4Q"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/internal-server-error",
  "title": "Internal Server Error",
  "status": 500,
  "detail": "An unexpected error occurred while processing the request.",
  "instance": "/api/v1/groups/req_01JZ4T8V6W5X4Y3Z2A1",
  "request_id": "req_01JZ4T8V6W5X4Y3Z2A1"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/authentication-required",
  "title": "Authentication Required",
  "status": 401,
  "detail": "Access token is missing or expired.",
  "instance": "/api/v1/users/me/req_01JZ4T3Q8R9A2F4M7N6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/permission-denied",
  "title": "Permission Denied",
  "status": 403,
  "detail": "You cannot access another user's private resource.",
  "instance": "/api/v1/groups/01ARZ3NDEKTSV4RRFFQ69G5FAV8/req_01JZ4T4H1X2B3C4D5E6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/resource-not-found",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "Requested resource does not exist.",
  "instance": "/api/v1/tasks/01ARZ3NDEKTSV4RRFFQ69G5FAV9/req_01JZ4T59K8L7M6N5P4Q"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/internal-server-error",
  "title": "Internal Server Error",
  "status": 500,
  "detail": "An unexpected error occurred while processing the request.",
  "instance": "/api/v1/groups/req_01JZ4T8V6W5X4Y3Z2A1",
  "request_id": "req_01JZ4T8V6W5X4Y3Z2A1"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/authentication-required",
  "title": "Authentication Required",
  "status": 401,
  "detail": "Access token is missing or expired.",
  "instance": "/api/v1/users/me/req_01JZ4T3Q8R9A2F4M7N6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/permission-denied",
  "title": "Permission Denied",
  "status": 403,
  "detail": "You cannot access another user's private resource.",
  "instance": "/api/v1/groups/01ARZ3NDEKTSV4RRFFQ69G5FAV8/req_01JZ4T4H1X2B3C4D5E6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/resource-not-found",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "Requested resource does not exist.",
  "instance": "/api/v1/tasks/01ARZ3NDEKTSV4RRFFQ69G5FAV9/req_01JZ4T59K8L7M6N5P4Q"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/internal-server-error",
  "title": "Internal Server Error",
  "status": 500,
  "detail": "An unexpected error occurred while processing the request.",
  "instance": "/api/v1/groups/req_01JZ4T8V6W5X4Y3Z2A1",
  "request_id": "req_01JZ4T8V6W5X4Y3Z2A1"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/authentication-required",
  "title": "Authentication Required",
  "status": 401,
  "detail": "Access token is missing or expired.",
  "instance": "/api/v1/users/me/req_01JZ4T3Q8R9A2F4M7N6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/permission-denied",
  "title": "Permission Denied",
  "status": 403,
  "detail": "You cannot access another user's private resource.",
  "instance": "/api/v1/groups/01ARZ3NDEKTSV4RRFFQ69G5FAV8/req_01JZ4T4H1X2B3C4D5E6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/resource-not-found",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "Requested resource does not exist.",
  "instance": "/api/v1/tasks/01ARZ3NDEKTSV4RRFFQ69G5FAV9/req_01JZ4T59K8L7M6N5P4Q"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/internal-server-error",
  "title": "Internal Server Error",
  "status": 500,
  "detail": "An unexpected error occurred while processing the request.",
  "instance": "/api/v1/groups/req_01JZ4T8V6W5X4Y3Z2A1",
  "request_id": "req_01JZ4T8V6W5X4Y3Z2A1"
}
```

<h3 id="deletegroup-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|204|[No Content](https://tools.ietf.org/html/rfc7231#section-6.3.5)|Group 삭제 완료, 연결된 task를 `group_id = null`로 재할당.|None|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|RFC9457 problem details 응답.|[Problem](#schemaproblem)|
|403|[Forbidden](https://tools.ietf.org/html/rfc7231#section-6.5.3)|RFC9457 problem details 응답.|[Problem](#schemaproblem)|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|RFC9457 problem details 응답.|[Problem](#schemaproblem)|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|RFC9457 problem details 응답.|[Problem](#schemaproblem)|
|429|[Too Many Requests](https://tools.ietf.org/html/rfc6585#section-4)|RFC9457 problem details 응답.|[Problem](#schemaproblem)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|RFC9457 problem details 응답.|[Problem](#schemaproblem)|

### Response Headers

|Status|Header|Type|Format|Description|
|---|---|---|---|---|
|204|X-Request-Id|string||이 성공 삭제 요청의 추적 id.|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

<h1 id="o-nlogn-api-v1-tasks">tasks</h1>

Task CRUD 및 조회 작업.

## listTasks

<a id="opIdlistTasks"></a>

> Code samples

```shell
# You can also use wget
curl -X GET https://api.onlogn.com/api/v1/tasks \
  -H 'Accept: application/json' \
  -H 'Authorization: Bearer {access-token}'

```

```http
GET https://api.onlogn.com/api/v1/tasks HTTP/1.1
Host: api.onlogn.com
Accept: application/json

```

```javascript

const headers = {
  'Accept':'application/json',
  'Authorization':'Bearer {access-token}'
};

fetch('https://api.onlogn.com/api/v1/tasks',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

```ruby
require 'rest-client'
require 'json'

headers = {
  'Accept' => 'application/json',
  'Authorization' => 'Bearer {access-token}'
}

result = RestClient.get 'https://api.onlogn.com/api/v1/tasks',
  params: {
  }, headers: headers

p JSON.parse(result)

```

```python
import requests
headers = {
  'Accept': 'application/json',
  'Authorization': 'Bearer {access-token}'
}

r = requests.get('https://api.onlogn.com/api/v1/tasks', headers = headers)

print(r.json())

```

```php
<?php

require 'vendor/autoload.php';

$headers = array(
    'Accept' => 'application/json',
    'Authorization' => 'Bearer {access-token}',
);

$client = new \GuzzleHttp\Client();

// Define array of request body.
$request_body = array();

try {
    $response = $client->request('GET','https://api.onlogn.com/api/v1/tasks', array(
        'headers' => $headers,
        'json' => $request_body,
       )
    );
    print_r($response->getBody()->getContents());
 }
 catch (\GuzzleHttp\Exception\BadResponseException $e) {
    // handle exception or api errors.
    print_r($e->getMessage());
 }

 // ...

```

```java
URL obj = new URL("https://api.onlogn.com/api/v1/tasks");
HttpURLConnection con = (HttpURLConnection) obj.openConnection();
con.setRequestMethod("GET");
int responseCode = con.getResponseCode();
BufferedReader in = new BufferedReader(
    new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer response = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
}
in.close();
System.out.println(response.toString());

```

```go
package main

import (
       "bytes"
       "net/http"
)

func main() {

    headers := map[string][]string{
        "Accept": []string{"application/json"},
        "Authorization": []string{"Bearer {access-token}"},
    }

    data := bytes.NewBuffer([]byte{jsonReq})
    req, err := http.NewRequest("GET", "https://api.onlogn.com/api/v1/tasks", data)
    req.Header = headers

    client := &http.Client{}
    resp, err := client.Do(req)
    // ...
}

```

`GET /api/v1/tasks`

*owner task 목록 조회*

인증 사용자를 위한 owner 범위 task 목록.
기준 정렬은 `created_at desc`, 타이브레이커는 `id desc`다.

<h3 id="listtasks-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|offset|query|integer|false|목록 페이지네이션용 0 기반 offset.|
|limit|query|integer|false|목록 페이지네이션용 페이지 크기.|
|status|query|[TaskStatus](#schemataskstatus)|false|none|
|visibility|query|[TaskVisibility](#schemataskvisibility)|false|none|
|group_id|query|any|false|그룹 미지정 task를 위한 UUID group id 또는 리터럴 `null`.|
|due_date_from|query|string(date)|false|none|
|due_date_to|query|string(date)|false|none|
|sort|query|string|false|v1에서는 `created_at desc`만 허용한다.|

#### Enumerated Values

|Parameter|Value|
|---|---|
|status|todo|
|status|in_progress|
|status|done|
|visibility|private|
|visibility|public|
|sort|created_at desc|

> Example responses

> 200 Response

```json
{
  "data": [
    {
      "id": "123e4567-e89b-12d3-a456-426614174002",
      "owner_user_id": "123e4567-e89b-12d3-a456-426614174000",
      "group_id": "123e4567-e89b-12d3-a456-426614174001",
      "title": "Prepare sprint handoff",
      "status": "in_progress",
      "visibility": "private",
      "created_at": "2026-02-10T09:00:00Z",
      "updated_at": "2026-02-10T10:30:00Z",
      "due_date": "2026-02-12",
      "start_time": "2026-02-10T09:00:00Z",
      "end_time": null,
      "tags": [
        "planning",
        "backend"
      ],
      "reference_links": [
        "https://docs.onlogn.com/specs/sprint-handoff"
      ]
    }
  ],
  "meta": {
    "pagination": {
      "offset": 0,
      "limit": 20,
      "total": 1,
      "sort": "created_at desc, id desc"
    }
  }
}
```

> RFC9457 problem details 응답.

```json
{
  "type": "https://api.onlogn.com/problems/authentication-required",
  "title": "Authentication Required",
  "status": 401,
  "detail": "Access token is missing or expired.",
  "instance": "/api/v1/users/me/req_01JZ4T3Q8R9A2F4M7N6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/permission-denied",
  "title": "Permission Denied",
  "status": 403,
  "detail": "You cannot access another user's private resource.",
  "instance": "/api/v1/groups/01ARZ3NDEKTSV4RRFFQ69G5FAV8/req_01JZ4T4H1X2B3C4D5E6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/resource-not-found",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "Requested resource does not exist.",
  "instance": "/api/v1/tasks/01ARZ3NDEKTSV4RRFFQ69G5FAV9/req_01JZ4T59K8L7M6N5P4Q"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/internal-server-error",
  "title": "Internal Server Error",
  "status": 500,
  "detail": "An unexpected error occurred while processing the request.",
  "instance": "/api/v1/groups/req_01JZ4T8V6W5X4Y3Z2A1",
  "request_id": "req_01JZ4T8V6W5X4Y3Z2A1"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/authentication-required",
  "title": "Authentication Required",
  "status": 401,
  "detail": "Access token is missing or expired.",
  "instance": "/api/v1/users/me/req_01JZ4T3Q8R9A2F4M7N6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/permission-denied",
  "title": "Permission Denied",
  "status": 403,
  "detail": "You cannot access another user's private resource.",
  "instance": "/api/v1/groups/01ARZ3NDEKTSV4RRFFQ69G5FAV8/req_01JZ4T4H1X2B3C4D5E6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/resource-not-found",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "Requested resource does not exist.",
  "instance": "/api/v1/tasks/01ARZ3NDEKTSV4RRFFQ69G5FAV9/req_01JZ4T59K8L7M6N5P4Q"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/internal-server-error",
  "title": "Internal Server Error",
  "status": 500,
  "detail": "An unexpected error occurred while processing the request.",
  "instance": "/api/v1/groups/req_01JZ4T8V6W5X4Y3Z2A1",
  "request_id": "req_01JZ4T8V6W5X4Y3Z2A1"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/authentication-required",
  "title": "Authentication Required",
  "status": 401,
  "detail": "Access token is missing or expired.",
  "instance": "/api/v1/users/me/req_01JZ4T3Q8R9A2F4M7N6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/permission-denied",
  "title": "Permission Denied",
  "status": 403,
  "detail": "You cannot access another user's private resource.",
  "instance": "/api/v1/groups/01ARZ3NDEKTSV4RRFFQ69G5FAV8/req_01JZ4T4H1X2B3C4D5E6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/resource-not-found",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "Requested resource does not exist.",
  "instance": "/api/v1/tasks/01ARZ3NDEKTSV4RRFFQ69G5FAV9/req_01JZ4T59K8L7M6N5P4Q"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/internal-server-error",
  "title": "Internal Server Error",
  "status": 500,
  "detail": "An unexpected error occurred while processing the request.",
  "instance": "/api/v1/groups/req_01JZ4T8V6W5X4Y3Z2A1",
  "request_id": "req_01JZ4T8V6W5X4Y3Z2A1"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/authentication-required",
  "title": "Authentication Required",
  "status": 401,
  "detail": "Access token is missing or expired.",
  "instance": "/api/v1/users/me/req_01JZ4T3Q8R9A2F4M7N6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/permission-denied",
  "title": "Permission Denied",
  "status": 403,
  "detail": "You cannot access another user's private resource.",
  "instance": "/api/v1/groups/01ARZ3NDEKTSV4RRFFQ69G5FAV8/req_01JZ4T4H1X2B3C4D5E6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/resource-not-found",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "Requested resource does not exist.",
  "instance": "/api/v1/tasks/01ARZ3NDEKTSV4RRFFQ69G5FAV9/req_01JZ4T59K8L7M6N5P4Q"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/internal-server-error",
  "title": "Internal Server Error",
  "status": 500,
  "detail": "An unexpected error occurred while processing the request.",
  "instance": "/api/v1/groups/req_01JZ4T8V6W5X4Y3Z2A1",
  "request_id": "req_01JZ4T8V6W5X4Y3Z2A1"
}
```

<h3 id="listtasks-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|Task 목록.|[TaskListResponse](#schematasklistresponse)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|RFC9457 problem details 응답.|[Problem](#schemaproblem)|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|RFC9457 problem details 응답.|[Problem](#schemaproblem)|
|403|[Forbidden](https://tools.ietf.org/html/rfc7231#section-6.5.3)|RFC9457 problem details 응답.|[Problem](#schemaproblem)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|RFC9457 problem details 응답.|[Problem](#schemaproblem)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## createTask

<a id="opIdcreateTask"></a>

> Code samples

```shell
# You can also use wget
curl -X POST https://api.onlogn.com/api/v1/tasks \
  -H 'Content-Type: application/json' \
  -H 'Accept: application/json' \
  -H 'Authorization: Bearer {access-token}'

```

```http
POST https://api.onlogn.com/api/v1/tasks HTTP/1.1
Host: api.onlogn.com
Content-Type: application/json
Accept: application/json

```

```javascript
const inputBody = '{
  "group_id": "306db4e0-7449-4501-b76f-075576fe2d8f",
  "title": "string",
  "status": "todo",
  "visibility": "private",
  "due_date": "2019-08-24",
  "start_time": "2019-08-24T14:15:22Z",
  "end_time": "2019-08-24T14:15:22Z",
  "tags": [
    "string"
  ],
  "reference_links": [
    "http://example.com"
  ]
}';
const headers = {
  'Content-Type':'application/json',
  'Accept':'application/json',
  'Authorization':'Bearer {access-token}'
};

fetch('https://api.onlogn.com/api/v1/tasks',
{
  method: 'POST',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

```ruby
require 'rest-client'
require 'json'

headers = {
  'Content-Type' => 'application/json',
  'Accept' => 'application/json',
  'Authorization' => 'Bearer {access-token}'
}

result = RestClient.post 'https://api.onlogn.com/api/v1/tasks',
  params: {
  }, headers: headers

p JSON.parse(result)

```

```python
import requests
headers = {
  'Content-Type': 'application/json',
  'Accept': 'application/json',
  'Authorization': 'Bearer {access-token}'
}

r = requests.post('https://api.onlogn.com/api/v1/tasks', headers = headers)

print(r.json())

```

```php
<?php

require 'vendor/autoload.php';

$headers = array(
    'Content-Type' => 'application/json',
    'Accept' => 'application/json',
    'Authorization' => 'Bearer {access-token}',
);

$client = new \GuzzleHttp\Client();

// Define array of request body.
$request_body = array();

try {
    $response = $client->request('POST','https://api.onlogn.com/api/v1/tasks', array(
        'headers' => $headers,
        'json' => $request_body,
       )
    );
    print_r($response->getBody()->getContents());
 }
 catch (\GuzzleHttp\Exception\BadResponseException $e) {
    // handle exception or api errors.
    print_r($e->getMessage());
 }

 // ...

```

```java
URL obj = new URL("https://api.onlogn.com/api/v1/tasks");
HttpURLConnection con = (HttpURLConnection) obj.openConnection();
con.setRequestMethod("POST");
int responseCode = con.getResponseCode();
BufferedReader in = new BufferedReader(
    new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer response = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
}
in.close();
System.out.println(response.toString());

```

```go
package main

import (
       "bytes"
       "net/http"
)

func main() {

    headers := map[string][]string{
        "Content-Type": []string{"application/json"},
        "Accept": []string{"application/json"},
        "Authorization": []string{"Bearer {access-token}"},
    }

    data := bytes.NewBuffer([]byte{jsonReq})
    req, err := http.NewRequest("POST", "https://api.onlogn.com/api/v1/tasks", data)
    req.Header = headers

    client := &http.Client{}
    resp, err := client.Do(req)
    // ...
}

```

`POST /api/v1/tasks`

*owner task 생성*

인증 owner 범위에서 task를 생성한다.

> Body parameter

```json
{
  "group_id": "306db4e0-7449-4501-b76f-075576fe2d8f",
  "title": "string",
  "status": "todo",
  "visibility": "private",
  "due_date": "2019-08-24",
  "start_time": "2019-08-24T14:15:22Z",
  "end_time": "2019-08-24T14:15:22Z",
  "tags": [
    "string"
  ],
  "reference_links": [
    "http://example.com"
  ]
}
```

<h3 id="createtask-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|body|body|[TaskCreateRequest](#schemataskcreaterequest)|true|none|

> Example responses

> 201 Response

```json
{
  "data": {
    "id": "123e4567-e89b-12d3-a456-426614174002",
    "owner_user_id": "123e4567-e89b-12d3-a456-426614174000",
    "group_id": "123e4567-e89b-12d3-a456-426614174001",
    "title": "Prepare sprint handoff",
    "status": "in_progress",
    "visibility": "private",
    "created_at": "2026-02-10T09:00:00Z",
    "updated_at": "2026-02-10T10:30:00Z",
    "due_date": "2026-02-12",
    "start_time": "2026-02-10T09:00:00Z",
    "end_time": null,
    "tags": [
      "planning",
      "backend"
    ],
    "reference_links": [
      "https://docs.onlogn.com/specs/sprint-handoff"
    ]
  }
}
```

> RFC9457 problem details 응답.

```json
{
  "type": "https://api.onlogn.com/problems/authentication-required",
  "title": "Authentication Required",
  "status": 401,
  "detail": "Access token is missing or expired.",
  "instance": "/api/v1/users/me/req_01JZ4T3Q8R9A2F4M7N6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/permission-denied",
  "title": "Permission Denied",
  "status": 403,
  "detail": "You cannot access another user's private resource.",
  "instance": "/api/v1/groups/01ARZ3NDEKTSV4RRFFQ69G5FAV8/req_01JZ4T4H1X2B3C4D5E6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/resource-not-found",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "Requested resource does not exist.",
  "instance": "/api/v1/tasks/01ARZ3NDEKTSV4RRFFQ69G5FAV9/req_01JZ4T59K8L7M6N5P4Q"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/internal-server-error",
  "title": "Internal Server Error",
  "status": 500,
  "detail": "An unexpected error occurred while processing the request.",
  "instance": "/api/v1/groups/req_01JZ4T8V6W5X4Y3Z2A1",
  "request_id": "req_01JZ4T8V6W5X4Y3Z2A1"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/authentication-required",
  "title": "Authentication Required",
  "status": 401,
  "detail": "Access token is missing or expired.",
  "instance": "/api/v1/users/me/req_01JZ4T3Q8R9A2F4M7N6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/permission-denied",
  "title": "Permission Denied",
  "status": 403,
  "detail": "You cannot access another user's private resource.",
  "instance": "/api/v1/groups/01ARZ3NDEKTSV4RRFFQ69G5FAV8/req_01JZ4T4H1X2B3C4D5E6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/resource-not-found",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "Requested resource does not exist.",
  "instance": "/api/v1/tasks/01ARZ3NDEKTSV4RRFFQ69G5FAV9/req_01JZ4T59K8L7M6N5P4Q"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/internal-server-error",
  "title": "Internal Server Error",
  "status": 500,
  "detail": "An unexpected error occurred while processing the request.",
  "instance": "/api/v1/groups/req_01JZ4T8V6W5X4Y3Z2A1",
  "request_id": "req_01JZ4T8V6W5X4Y3Z2A1"
}
```

> 422 Response

```json
{
  "type": "https://api.onlogn.com/problems/validation-failed",
  "title": "Validation Failed",
  "status": 422,
  "detail": "Request body validation failed.",
  "instance": "/api/v1/tasks/req_01JZ4T59K8L7M6N5P4Q",
  "errors": [
    {
      "field": "status",
      "reason": "must be one of: todo, in_progress, done"
    },
    {
      "field": "due_date",
      "reason": "must be a valid date in YYYY-MM-DD format"
    }
  ]
}
```

> RFC9457 problem details 응답.

```json
{
  "type": "https://api.onlogn.com/problems/authentication-required",
  "title": "Authentication Required",
  "status": 401,
  "detail": "Access token is missing or expired.",
  "instance": "/api/v1/users/me/req_01JZ4T3Q8R9A2F4M7N6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/permission-denied",
  "title": "Permission Denied",
  "status": 403,
  "detail": "You cannot access another user's private resource.",
  "instance": "/api/v1/groups/01ARZ3NDEKTSV4RRFFQ69G5FAV8/req_01JZ4T4H1X2B3C4D5E6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/resource-not-found",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "Requested resource does not exist.",
  "instance": "/api/v1/tasks/01ARZ3NDEKTSV4RRFFQ69G5FAV9/req_01JZ4T59K8L7M6N5P4Q"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/internal-server-error",
  "title": "Internal Server Error",
  "status": 500,
  "detail": "An unexpected error occurred while processing the request.",
  "instance": "/api/v1/groups/req_01JZ4T8V6W5X4Y3Z2A1",
  "request_id": "req_01JZ4T8V6W5X4Y3Z2A1"
}
```

<h3 id="createtask-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|201|[Created](https://tools.ietf.org/html/rfc7231#section-6.3.2)|Task 생성 완료.|[TaskResponse](#schemataskresponse)|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|RFC9457 problem details 응답.|[Problem](#schemaproblem)|
|403|[Forbidden](https://tools.ietf.org/html/rfc7231#section-6.5.3)|RFC9457 problem details 응답.|[Problem](#schemaproblem)|
|422|[Unprocessable Entity](https://tools.ietf.org/html/rfc2518#section-10.3)|RFC9457 validation problem details 응답.|[ValidationProblem](#schemavalidationproblem)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|RFC9457 problem details 응답.|[Problem](#schemaproblem)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## getTask

<a id="opIdgetTask"></a>

> Code samples

```shell
# You can also use wget
curl -X GET https://api.onlogn.com/api/v1/tasks/{task_id} \
  -H 'Accept: application/json' \
  -H 'Authorization: Bearer {access-token}'

```

```http
GET https://api.onlogn.com/api/v1/tasks/{task_id} HTTP/1.1
Host: api.onlogn.com
Accept: application/json

```

```javascript

const headers = {
  'Accept':'application/json',
  'Authorization':'Bearer {access-token}'
};

fetch('https://api.onlogn.com/api/v1/tasks/{task_id}',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

```ruby
require 'rest-client'
require 'json'

headers = {
  'Accept' => 'application/json',
  'Authorization' => 'Bearer {access-token}'
}

result = RestClient.get 'https://api.onlogn.com/api/v1/tasks/{task_id}',
  params: {
  }, headers: headers

p JSON.parse(result)

```

```python
import requests
headers = {
  'Accept': 'application/json',
  'Authorization': 'Bearer {access-token}'
}

r = requests.get('https://api.onlogn.com/api/v1/tasks/{task_id}', headers = headers)

print(r.json())

```

```php
<?php

require 'vendor/autoload.php';

$headers = array(
    'Accept' => 'application/json',
    'Authorization' => 'Bearer {access-token}',
);

$client = new \GuzzleHttp\Client();

// Define array of request body.
$request_body = array();

try {
    $response = $client->request('GET','https://api.onlogn.com/api/v1/tasks/{task_id}', array(
        'headers' => $headers,
        'json' => $request_body,
       )
    );
    print_r($response->getBody()->getContents());
 }
 catch (\GuzzleHttp\Exception\BadResponseException $e) {
    // handle exception or api errors.
    print_r($e->getMessage());
 }

 // ...

```

```java
URL obj = new URL("https://api.onlogn.com/api/v1/tasks/{task_id}");
HttpURLConnection con = (HttpURLConnection) obj.openConnection();
con.setRequestMethod("GET");
int responseCode = con.getResponseCode();
BufferedReader in = new BufferedReader(
    new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer response = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
}
in.close();
System.out.println(response.toString());

```

```go
package main

import (
       "bytes"
       "net/http"
)

func main() {

    headers := map[string][]string{
        "Accept": []string{"application/json"},
        "Authorization": []string{"Bearer {access-token}"},
    }

    data := bytes.NewBuffer([]byte{jsonReq})
    req, err := http.NewRequest("GET", "https://api.onlogn.com/api/v1/tasks/{task_id}", data)
    req.Header = headers

    client := &http.Client{}
    resp, err := client.Do(req)
    // ...
}

```

`GET /api/v1/tasks/{task_id}`

*owner task 조회*

owner 범위 단일 task를 조회한다.

<h3 id="gettask-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|task_id|path|string(uuid)|true|Task 식별자.|

> Example responses

> 200 Response

```json
{
  "data": {
    "id": "123e4567-e89b-12d3-a456-426614174002",
    "owner_user_id": "123e4567-e89b-12d3-a456-426614174000",
    "group_id": "123e4567-e89b-12d3-a456-426614174001",
    "title": "Prepare sprint handoff",
    "status": "in_progress",
    "visibility": "private",
    "created_at": "2026-02-10T09:00:00Z",
    "updated_at": "2026-02-10T10:30:00Z",
    "due_date": "2026-02-12",
    "start_time": "2026-02-10T09:00:00Z",
    "end_time": null,
    "tags": [
      "planning",
      "backend"
    ],
    "reference_links": [
      "https://docs.onlogn.com/specs/sprint-handoff"
    ]
  }
}
```

> RFC9457 problem details 응답.

```json
{
  "type": "https://api.onlogn.com/problems/authentication-required",
  "title": "Authentication Required",
  "status": 401,
  "detail": "Access token is missing or expired.",
  "instance": "/api/v1/users/me/req_01JZ4T3Q8R9A2F4M7N6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/permission-denied",
  "title": "Permission Denied",
  "status": 403,
  "detail": "You cannot access another user's private resource.",
  "instance": "/api/v1/groups/01ARZ3NDEKTSV4RRFFQ69G5FAV8/req_01JZ4T4H1X2B3C4D5E6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/resource-not-found",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "Requested resource does not exist.",
  "instance": "/api/v1/tasks/01ARZ3NDEKTSV4RRFFQ69G5FAV9/req_01JZ4T59K8L7M6N5P4Q"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/internal-server-error",
  "title": "Internal Server Error",
  "status": 500,
  "detail": "An unexpected error occurred while processing the request.",
  "instance": "/api/v1/groups/req_01JZ4T8V6W5X4Y3Z2A1",
  "request_id": "req_01JZ4T8V6W5X4Y3Z2A1"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/authentication-required",
  "title": "Authentication Required",
  "status": 401,
  "detail": "Access token is missing or expired.",
  "instance": "/api/v1/users/me/req_01JZ4T3Q8R9A2F4M7N6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/permission-denied",
  "title": "Permission Denied",
  "status": 403,
  "detail": "You cannot access another user's private resource.",
  "instance": "/api/v1/groups/01ARZ3NDEKTSV4RRFFQ69G5FAV8/req_01JZ4T4H1X2B3C4D5E6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/resource-not-found",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "Requested resource does not exist.",
  "instance": "/api/v1/tasks/01ARZ3NDEKTSV4RRFFQ69G5FAV9/req_01JZ4T59K8L7M6N5P4Q"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/internal-server-error",
  "title": "Internal Server Error",
  "status": 500,
  "detail": "An unexpected error occurred while processing the request.",
  "instance": "/api/v1/groups/req_01JZ4T8V6W5X4Y3Z2A1",
  "request_id": "req_01JZ4T8V6W5X4Y3Z2A1"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/authentication-required",
  "title": "Authentication Required",
  "status": 401,
  "detail": "Access token is missing or expired.",
  "instance": "/api/v1/users/me/req_01JZ4T3Q8R9A2F4M7N6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/permission-denied",
  "title": "Permission Denied",
  "status": 403,
  "detail": "You cannot access another user's private resource.",
  "instance": "/api/v1/groups/01ARZ3NDEKTSV4RRFFQ69G5FAV8/req_01JZ4T4H1X2B3C4D5E6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/resource-not-found",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "Requested resource does not exist.",
  "instance": "/api/v1/tasks/01ARZ3NDEKTSV4RRFFQ69G5FAV9/req_01JZ4T59K8L7M6N5P4Q"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/internal-server-error",
  "title": "Internal Server Error",
  "status": 500,
  "detail": "An unexpected error occurred while processing the request.",
  "instance": "/api/v1/groups/req_01JZ4T8V6W5X4Y3Z2A1",
  "request_id": "req_01JZ4T8V6W5X4Y3Z2A1"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/authentication-required",
  "title": "Authentication Required",
  "status": 401,
  "detail": "Access token is missing or expired.",
  "instance": "/api/v1/users/me/req_01JZ4T3Q8R9A2F4M7N6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/permission-denied",
  "title": "Permission Denied",
  "status": 403,
  "detail": "You cannot access another user's private resource.",
  "instance": "/api/v1/groups/01ARZ3NDEKTSV4RRFFQ69G5FAV8/req_01JZ4T4H1X2B3C4D5E6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/resource-not-found",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "Requested resource does not exist.",
  "instance": "/api/v1/tasks/01ARZ3NDEKTSV4RRFFQ69G5FAV9/req_01JZ4T59K8L7M6N5P4Q"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/internal-server-error",
  "title": "Internal Server Error",
  "status": 500,
  "detail": "An unexpected error occurred while processing the request.",
  "instance": "/api/v1/groups/req_01JZ4T8V6W5X4Y3Z2A1",
  "request_id": "req_01JZ4T8V6W5X4Y3Z2A1"
}
```

<h3 id="gettask-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|Task 상세.|[TaskResponse](#schemataskresponse)|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|RFC9457 problem details 응답.|[Problem](#schemaproblem)|
|403|[Forbidden](https://tools.ietf.org/html/rfc7231#section-6.5.3)|RFC9457 problem details 응답.|[Problem](#schemaproblem)|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|RFC9457 problem details 응답.|[Problem](#schemaproblem)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|RFC9457 problem details 응답.|[Problem](#schemaproblem)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## updateTask

<a id="opIdupdateTask"></a>

> Code samples

```shell
# You can also use wget
curl -X PATCH https://api.onlogn.com/api/v1/tasks/{task_id} \
  -H 'Content-Type: application/json' \
  -H 'Accept: application/json' \
  -H 'Authorization: Bearer {access-token}'

```

```http
PATCH https://api.onlogn.com/api/v1/tasks/{task_id} HTTP/1.1
Host: api.onlogn.com
Content-Type: application/json
Accept: application/json

```

```javascript
const inputBody = '{
  "group_id": "306db4e0-7449-4501-b76f-075576fe2d8f",
  "title": "string",
  "status": "todo",
  "visibility": "private",
  "due_date": "2019-08-24",
  "start_time": "2019-08-24T14:15:22Z",
  "end_time": "2019-08-24T14:15:22Z",
  "tags": [
    "string"
  ],
  "reference_links": [
    "http://example.com"
  ]
}';
const headers = {
  'Content-Type':'application/json',
  'Accept':'application/json',
  'Authorization':'Bearer {access-token}'
};

fetch('https://api.onlogn.com/api/v1/tasks/{task_id}',
{
  method: 'PATCH',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

```ruby
require 'rest-client'
require 'json'

headers = {
  'Content-Type' => 'application/json',
  'Accept' => 'application/json',
  'Authorization' => 'Bearer {access-token}'
}

result = RestClient.patch 'https://api.onlogn.com/api/v1/tasks/{task_id}',
  params: {
  }, headers: headers

p JSON.parse(result)

```

```python
import requests
headers = {
  'Content-Type': 'application/json',
  'Accept': 'application/json',
  'Authorization': 'Bearer {access-token}'
}

r = requests.patch('https://api.onlogn.com/api/v1/tasks/{task_id}', headers = headers)

print(r.json())

```

```php
<?php

require 'vendor/autoload.php';

$headers = array(
    'Content-Type' => 'application/json',
    'Accept' => 'application/json',
    'Authorization' => 'Bearer {access-token}',
);

$client = new \GuzzleHttp\Client();

// Define array of request body.
$request_body = array();

try {
    $response = $client->request('PATCH','https://api.onlogn.com/api/v1/tasks/{task_id}', array(
        'headers' => $headers,
        'json' => $request_body,
       )
    );
    print_r($response->getBody()->getContents());
 }
 catch (\GuzzleHttp\Exception\BadResponseException $e) {
    // handle exception or api errors.
    print_r($e->getMessage());
 }

 // ...

```

```java
URL obj = new URL("https://api.onlogn.com/api/v1/tasks/{task_id}");
HttpURLConnection con = (HttpURLConnection) obj.openConnection();
con.setRequestMethod("PATCH");
int responseCode = con.getResponseCode();
BufferedReader in = new BufferedReader(
    new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer response = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
}
in.close();
System.out.println(response.toString());

```

```go
package main

import (
       "bytes"
       "net/http"
)

func main() {

    headers := map[string][]string{
        "Content-Type": []string{"application/json"},
        "Accept": []string{"application/json"},
        "Authorization": []string{"Bearer {access-token}"},
    }

    data := bytes.NewBuffer([]byte{jsonReq})
    req, err := http.NewRequest("PATCH", "https://api.onlogn.com/api/v1/tasks/{task_id}", data)
    req.Header = headers

    client := &http.Client{}
    resp, err := client.Do(req)
    // ...
}

```

`PATCH /api/v1/tasks/{task_id}`

*owner task 수정*

owner 범위 task의 변경 가능한 필드를 수정한다.

> Body parameter

```json
{
  "group_id": "306db4e0-7449-4501-b76f-075576fe2d8f",
  "title": "string",
  "status": "todo",
  "visibility": "private",
  "due_date": "2019-08-24",
  "start_time": "2019-08-24T14:15:22Z",
  "end_time": "2019-08-24T14:15:22Z",
  "tags": [
    "string"
  ],
  "reference_links": [
    "http://example.com"
  ]
}
```

<h3 id="updatetask-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|task_id|path|string(uuid)|true|Task 식별자.|
|body|body|[TaskUpdateRequest](#schemataskupdaterequest)|true|none|

> Example responses

> 200 Response

```json
{
  "data": {
    "id": "123e4567-e89b-12d3-a456-426614174002",
    "owner_user_id": "123e4567-e89b-12d3-a456-426614174000",
    "group_id": "123e4567-e89b-12d3-a456-426614174001",
    "title": "Prepare sprint handoff",
    "status": "in_progress",
    "visibility": "private",
    "created_at": "2026-02-10T09:00:00Z",
    "updated_at": "2026-02-10T10:30:00Z",
    "due_date": "2026-02-12",
    "start_time": "2026-02-10T09:00:00Z",
    "end_time": null,
    "tags": [
      "planning",
      "backend"
    ],
    "reference_links": [
      "https://docs.onlogn.com/specs/sprint-handoff"
    ]
  }
}
```

> RFC9457 problem details 응답.

```json
{
  "type": "https://api.onlogn.com/problems/authentication-required",
  "title": "Authentication Required",
  "status": 401,
  "detail": "Access token is missing or expired.",
  "instance": "/api/v1/users/me/req_01JZ4T3Q8R9A2F4M7N6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/permission-denied",
  "title": "Permission Denied",
  "status": 403,
  "detail": "You cannot access another user's private resource.",
  "instance": "/api/v1/groups/01ARZ3NDEKTSV4RRFFQ69G5FAV8/req_01JZ4T4H1X2B3C4D5E6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/resource-not-found",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "Requested resource does not exist.",
  "instance": "/api/v1/tasks/01ARZ3NDEKTSV4RRFFQ69G5FAV9/req_01JZ4T59K8L7M6N5P4Q"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/internal-server-error",
  "title": "Internal Server Error",
  "status": 500,
  "detail": "An unexpected error occurred while processing the request.",
  "instance": "/api/v1/groups/req_01JZ4T8V6W5X4Y3Z2A1",
  "request_id": "req_01JZ4T8V6W5X4Y3Z2A1"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/authentication-required",
  "title": "Authentication Required",
  "status": 401,
  "detail": "Access token is missing or expired.",
  "instance": "/api/v1/users/me/req_01JZ4T3Q8R9A2F4M7N6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/permission-denied",
  "title": "Permission Denied",
  "status": 403,
  "detail": "You cannot access another user's private resource.",
  "instance": "/api/v1/groups/01ARZ3NDEKTSV4RRFFQ69G5FAV8/req_01JZ4T4H1X2B3C4D5E6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/resource-not-found",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "Requested resource does not exist.",
  "instance": "/api/v1/tasks/01ARZ3NDEKTSV4RRFFQ69G5FAV9/req_01JZ4T59K8L7M6N5P4Q"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/internal-server-error",
  "title": "Internal Server Error",
  "status": 500,
  "detail": "An unexpected error occurred while processing the request.",
  "instance": "/api/v1/groups/req_01JZ4T8V6W5X4Y3Z2A1",
  "request_id": "req_01JZ4T8V6W5X4Y3Z2A1"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/authentication-required",
  "title": "Authentication Required",
  "status": 401,
  "detail": "Access token is missing or expired.",
  "instance": "/api/v1/users/me/req_01JZ4T3Q8R9A2F4M7N6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/permission-denied",
  "title": "Permission Denied",
  "status": 403,
  "detail": "You cannot access another user's private resource.",
  "instance": "/api/v1/groups/01ARZ3NDEKTSV4RRFFQ69G5FAV8/req_01JZ4T4H1X2B3C4D5E6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/resource-not-found",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "Requested resource does not exist.",
  "instance": "/api/v1/tasks/01ARZ3NDEKTSV4RRFFQ69G5FAV9/req_01JZ4T59K8L7M6N5P4Q"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/internal-server-error",
  "title": "Internal Server Error",
  "status": 500,
  "detail": "An unexpected error occurred while processing the request.",
  "instance": "/api/v1/groups/req_01JZ4T8V6W5X4Y3Z2A1",
  "request_id": "req_01JZ4T8V6W5X4Y3Z2A1"
}
```

> 422 Response

```json
{
  "type": "https://api.onlogn.com/problems/validation-failed",
  "title": "Validation Failed",
  "status": 422,
  "detail": "Request body validation failed.",
  "instance": "/api/v1/tasks/req_01JZ4T59K8L7M6N5P4Q",
  "errors": [
    {
      "field": "status",
      "reason": "must be one of: todo, in_progress, done"
    },
    {
      "field": "due_date",
      "reason": "must be a valid date in YYYY-MM-DD format"
    }
  ]
}
```

> RFC9457 problem details 응답.

```json
{
  "type": "https://api.onlogn.com/problems/authentication-required",
  "title": "Authentication Required",
  "status": 401,
  "detail": "Access token is missing or expired.",
  "instance": "/api/v1/users/me/req_01JZ4T3Q8R9A2F4M7N6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/permission-denied",
  "title": "Permission Denied",
  "status": 403,
  "detail": "You cannot access another user's private resource.",
  "instance": "/api/v1/groups/01ARZ3NDEKTSV4RRFFQ69G5FAV8/req_01JZ4T4H1X2B3C4D5E6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/resource-not-found",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "Requested resource does not exist.",
  "instance": "/api/v1/tasks/01ARZ3NDEKTSV4RRFFQ69G5FAV9/req_01JZ4T59K8L7M6N5P4Q"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/internal-server-error",
  "title": "Internal Server Error",
  "status": 500,
  "detail": "An unexpected error occurred while processing the request.",
  "instance": "/api/v1/groups/req_01JZ4T8V6W5X4Y3Z2A1",
  "request_id": "req_01JZ4T8V6W5X4Y3Z2A1"
}
```

<h3 id="updatetask-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|Task 수정 완료.|[TaskResponse](#schemataskresponse)|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|RFC9457 problem details 응답.|[Problem](#schemaproblem)|
|403|[Forbidden](https://tools.ietf.org/html/rfc7231#section-6.5.3)|RFC9457 problem details 응답.|[Problem](#schemaproblem)|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|RFC9457 problem details 응답.|[Problem](#schemaproblem)|
|422|[Unprocessable Entity](https://tools.ietf.org/html/rfc2518#section-10.3)|RFC9457 validation problem details 응답.|[ValidationProblem](#schemavalidationproblem)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|RFC9457 problem details 응답.|[Problem](#schemaproblem)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## deleteTask

<a id="opIddeleteTask"></a>

> Code samples

```shell
# You can also use wget
curl -X DELETE https://api.onlogn.com/api/v1/tasks/{task_id} \
  -H 'Accept: application/problem+json' \
  -H 'Authorization: Bearer {access-token}'

```

```http
DELETE https://api.onlogn.com/api/v1/tasks/{task_id} HTTP/1.1
Host: api.onlogn.com
Accept: application/problem+json

```

```javascript

const headers = {
  'Accept':'application/problem+json',
  'Authorization':'Bearer {access-token}'
};

fetch('https://api.onlogn.com/api/v1/tasks/{task_id}',
{
  method: 'DELETE',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

```ruby
require 'rest-client'
require 'json'

headers = {
  'Accept' => 'application/problem+json',
  'Authorization' => 'Bearer {access-token}'
}

result = RestClient.delete 'https://api.onlogn.com/api/v1/tasks/{task_id}',
  params: {
  }, headers: headers

p JSON.parse(result)

```

```python
import requests
headers = {
  'Accept': 'application/problem+json',
  'Authorization': 'Bearer {access-token}'
}

r = requests.delete('https://api.onlogn.com/api/v1/tasks/{task_id}', headers = headers)

print(r.json())

```

```php
<?php

require 'vendor/autoload.php';

$headers = array(
    'Accept' => 'application/problem+json',
    'Authorization' => 'Bearer {access-token}',
);

$client = new \GuzzleHttp\Client();

// Define array of request body.
$request_body = array();

try {
    $response = $client->request('DELETE','https://api.onlogn.com/api/v1/tasks/{task_id}', array(
        'headers' => $headers,
        'json' => $request_body,
       )
    );
    print_r($response->getBody()->getContents());
 }
 catch (\GuzzleHttp\Exception\BadResponseException $e) {
    // handle exception or api errors.
    print_r($e->getMessage());
 }

 // ...

```

```java
URL obj = new URL("https://api.onlogn.com/api/v1/tasks/{task_id}");
HttpURLConnection con = (HttpURLConnection) obj.openConnection();
con.setRequestMethod("DELETE");
int responseCode = con.getResponseCode();
BufferedReader in = new BufferedReader(
    new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer response = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
}
in.close();
System.out.println(response.toString());

```

```go
package main

import (
       "bytes"
       "net/http"
)

func main() {

    headers := map[string][]string{
        "Accept": []string{"application/problem+json"},
        "Authorization": []string{"Bearer {access-token}"},
    }

    data := bytes.NewBuffer([]byte{jsonReq})
    req, err := http.NewRequest("DELETE", "https://api.onlogn.com/api/v1/tasks/{task_id}", data)
    req.Header = headers

    client := &http.Client{}
    resp, err := client.Do(req)
    // ...
}

```

`DELETE /api/v1/tasks/{task_id}`

*owner task 삭제*

owner 범위 task를 삭제한다.

<h3 id="deletetask-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|task_id|path|string(uuid)|true|Task 식별자.|

> Example responses

> RFC9457 problem details 응답.

```json
{
  "type": "https://api.onlogn.com/problems/authentication-required",
  "title": "Authentication Required",
  "status": 401,
  "detail": "Access token is missing or expired.",
  "instance": "/api/v1/users/me/req_01JZ4T3Q8R9A2F4M7N6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/permission-denied",
  "title": "Permission Denied",
  "status": 403,
  "detail": "You cannot access another user's private resource.",
  "instance": "/api/v1/groups/01ARZ3NDEKTSV4RRFFQ69G5FAV8/req_01JZ4T4H1X2B3C4D5E6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/resource-not-found",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "Requested resource does not exist.",
  "instance": "/api/v1/tasks/01ARZ3NDEKTSV4RRFFQ69G5FAV9/req_01JZ4T59K8L7M6N5P4Q"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/internal-server-error",
  "title": "Internal Server Error",
  "status": 500,
  "detail": "An unexpected error occurred while processing the request.",
  "instance": "/api/v1/groups/req_01JZ4T8V6W5X4Y3Z2A1",
  "request_id": "req_01JZ4T8V6W5X4Y3Z2A1"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/authentication-required",
  "title": "Authentication Required",
  "status": 401,
  "detail": "Access token is missing or expired.",
  "instance": "/api/v1/users/me/req_01JZ4T3Q8R9A2F4M7N6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/permission-denied",
  "title": "Permission Denied",
  "status": 403,
  "detail": "You cannot access another user's private resource.",
  "instance": "/api/v1/groups/01ARZ3NDEKTSV4RRFFQ69G5FAV8/req_01JZ4T4H1X2B3C4D5E6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/resource-not-found",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "Requested resource does not exist.",
  "instance": "/api/v1/tasks/01ARZ3NDEKTSV4RRFFQ69G5FAV9/req_01JZ4T59K8L7M6N5P4Q"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/internal-server-error",
  "title": "Internal Server Error",
  "status": 500,
  "detail": "An unexpected error occurred while processing the request.",
  "instance": "/api/v1/groups/req_01JZ4T8V6W5X4Y3Z2A1",
  "request_id": "req_01JZ4T8V6W5X4Y3Z2A1"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/authentication-required",
  "title": "Authentication Required",
  "status": 401,
  "detail": "Access token is missing or expired.",
  "instance": "/api/v1/users/me/req_01JZ4T3Q8R9A2F4M7N6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/permission-denied",
  "title": "Permission Denied",
  "status": 403,
  "detail": "You cannot access another user's private resource.",
  "instance": "/api/v1/groups/01ARZ3NDEKTSV4RRFFQ69G5FAV8/req_01JZ4T4H1X2B3C4D5E6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/resource-not-found",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "Requested resource does not exist.",
  "instance": "/api/v1/tasks/01ARZ3NDEKTSV4RRFFQ69G5FAV9/req_01JZ4T59K8L7M6N5P4Q"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/internal-server-error",
  "title": "Internal Server Error",
  "status": 500,
  "detail": "An unexpected error occurred while processing the request.",
  "instance": "/api/v1/groups/req_01JZ4T8V6W5X4Y3Z2A1",
  "request_id": "req_01JZ4T8V6W5X4Y3Z2A1"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/authentication-required",
  "title": "Authentication Required",
  "status": 401,
  "detail": "Access token is missing or expired.",
  "instance": "/api/v1/users/me/req_01JZ4T3Q8R9A2F4M7N6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/permission-denied",
  "title": "Permission Denied",
  "status": 403,
  "detail": "You cannot access another user's private resource.",
  "instance": "/api/v1/groups/01ARZ3NDEKTSV4RRFFQ69G5FAV8/req_01JZ4T4H1X2B3C4D5E6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/resource-not-found",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "Requested resource does not exist.",
  "instance": "/api/v1/tasks/01ARZ3NDEKTSV4RRFFQ69G5FAV9/req_01JZ4T59K8L7M6N5P4Q"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/internal-server-error",
  "title": "Internal Server Error",
  "status": 500,
  "detail": "An unexpected error occurred while processing the request.",
  "instance": "/api/v1/groups/req_01JZ4T8V6W5X4Y3Z2A1",
  "request_id": "req_01JZ4T8V6W5X4Y3Z2A1"
}
```

<h3 id="deletetask-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|204|[No Content](https://tools.ietf.org/html/rfc7231#section-6.3.5)|Task 삭제 완료.|None|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|RFC9457 problem details 응답.|[Problem](#schemaproblem)|
|403|[Forbidden](https://tools.ietf.org/html/rfc7231#section-6.5.3)|RFC9457 problem details 응답.|[Problem](#schemaproblem)|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|RFC9457 problem details 응답.|[Problem](#schemaproblem)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|RFC9457 problem details 응답.|[Problem](#schemaproblem)|

### Response Headers

|Status|Header|Type|Format|Description|
|---|---|---|---|---|
|204|X-Request-Id|string||이 성공 삭제 요청의 추적 id.|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## getMonthlyTaskCalendar

<a id="opIdgetMonthlyTaskCalendar"></a>

> Code samples

```shell
# You can also use wget
curl -X GET https://api.onlogn.com/api/v1/tasks/calendar/monthly?year=1970&month=1 \
  -H 'Accept: application/json' \
  -H 'Authorization: Bearer {access-token}'

```

```http
GET https://api.onlogn.com/api/v1/tasks/calendar/monthly?year=1970&month=1 HTTP/1.1
Host: api.onlogn.com
Accept: application/json

```

```javascript

const headers = {
  'Accept':'application/json',
  'Authorization':'Bearer {access-token}'
};

fetch('https://api.onlogn.com/api/v1/tasks/calendar/monthly?year=1970&month=1',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

```ruby
require 'rest-client'
require 'json'

headers = {
  'Accept' => 'application/json',
  'Authorization' => 'Bearer {access-token}'
}

result = RestClient.get 'https://api.onlogn.com/api/v1/tasks/calendar/monthly',
  params: {
  'year' => 'integer',
'month' => 'integer'
}, headers: headers

p JSON.parse(result)

```

```python
import requests
headers = {
  'Accept': 'application/json',
  'Authorization': 'Bearer {access-token}'
}

r = requests.get('https://api.onlogn.com/api/v1/tasks/calendar/monthly', params={
  'year': '1970',  'month': '1'
}, headers = headers)

print(r.json())

```

```php
<?php

require 'vendor/autoload.php';

$headers = array(
    'Accept' => 'application/json',
    'Authorization' => 'Bearer {access-token}',
);

$client = new \GuzzleHttp\Client();

// Define array of request body.
$request_body = array();

try {
    $response = $client->request('GET','https://api.onlogn.com/api/v1/tasks/calendar/monthly', array(
        'headers' => $headers,
        'json' => $request_body,
       )
    );
    print_r($response->getBody()->getContents());
 }
 catch (\GuzzleHttp\Exception\BadResponseException $e) {
    // handle exception or api errors.
    print_r($e->getMessage());
 }

 // ...

```

```java
URL obj = new URL("https://api.onlogn.com/api/v1/tasks/calendar/monthly?year=1970&month=1");
HttpURLConnection con = (HttpURLConnection) obj.openConnection();
con.setRequestMethod("GET");
int responseCode = con.getResponseCode();
BufferedReader in = new BufferedReader(
    new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer response = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
}
in.close();
System.out.println(response.toString());

```

```go
package main

import (
       "bytes"
       "net/http"
)

func main() {

    headers := map[string][]string{
        "Accept": []string{"application/json"},
        "Authorization": []string{"Bearer {access-token}"},
    }

    data := bytes.NewBuffer([]byte{jsonReq})
    req, err := http.NewRequest("GET", "https://api.onlogn.com/api/v1/tasks/calendar/monthly", data)
    req.Header = headers

    client := &http.Client{}
    resp, err := client.Do(req)
    // ...
}

```

`GET /api/v1/tasks/calendar/monthly`

*월간 task 캘린더 조회*

요청한 `(year, month)`에 대해 0 값으로 채운 날짜를 포함해 완전한 일자 버킷을 반환한다.

<h3 id="getmonthlytaskcalendar-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|year|query|integer|true|none|
|month|query|integer|true|none|

> Example responses

> 200 Response

```json
{
  "data": [
    {
      "date": "2026-02-01",
      "planned_count": 2,
      "done_count": 1,
      "in_progress_count": 1,
      "completion_rate": 50
    },
    {
      "date": "2026-02-02",
      "planned_count": 0,
      "done_count": 0,
      "in_progress_count": 0,
      "completion_rate": 0
    }
  ],
  "meta": {
    "year": 2026,
    "month": 2,
    "timezone": "Asia/Seoul"
  }
}
```

> RFC9457 problem details 응답.

```json
{
  "type": "https://api.onlogn.com/problems/authentication-required",
  "title": "Authentication Required",
  "status": 401,
  "detail": "Access token is missing or expired.",
  "instance": "/api/v1/users/me/req_01JZ4T3Q8R9A2F4M7N6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/permission-denied",
  "title": "Permission Denied",
  "status": 403,
  "detail": "You cannot access another user's private resource.",
  "instance": "/api/v1/groups/01ARZ3NDEKTSV4RRFFQ69G5FAV8/req_01JZ4T4H1X2B3C4D5E6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/resource-not-found",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "Requested resource does not exist.",
  "instance": "/api/v1/tasks/01ARZ3NDEKTSV4RRFFQ69G5FAV9/req_01JZ4T59K8L7M6N5P4Q"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/internal-server-error",
  "title": "Internal Server Error",
  "status": 500,
  "detail": "An unexpected error occurred while processing the request.",
  "instance": "/api/v1/groups/req_01JZ4T8V6W5X4Y3Z2A1",
  "request_id": "req_01JZ4T8V6W5X4Y3Z2A1"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/authentication-required",
  "title": "Authentication Required",
  "status": 401,
  "detail": "Access token is missing or expired.",
  "instance": "/api/v1/users/me/req_01JZ4T3Q8R9A2F4M7N6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/permission-denied",
  "title": "Permission Denied",
  "status": 403,
  "detail": "You cannot access another user's private resource.",
  "instance": "/api/v1/groups/01ARZ3NDEKTSV4RRFFQ69G5FAV8/req_01JZ4T4H1X2B3C4D5E6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/resource-not-found",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "Requested resource does not exist.",
  "instance": "/api/v1/tasks/01ARZ3NDEKTSV4RRFFQ69G5FAV9/req_01JZ4T59K8L7M6N5P4Q"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/internal-server-error",
  "title": "Internal Server Error",
  "status": 500,
  "detail": "An unexpected error occurred while processing the request.",
  "instance": "/api/v1/groups/req_01JZ4T8V6W5X4Y3Z2A1",
  "request_id": "req_01JZ4T8V6W5X4Y3Z2A1"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/authentication-required",
  "title": "Authentication Required",
  "status": 401,
  "detail": "Access token is missing or expired.",
  "instance": "/api/v1/users/me/req_01JZ4T3Q8R9A2F4M7N6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/permission-denied",
  "title": "Permission Denied",
  "status": 403,
  "detail": "You cannot access another user's private resource.",
  "instance": "/api/v1/groups/01ARZ3NDEKTSV4RRFFQ69G5FAV8/req_01JZ4T4H1X2B3C4D5E6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/resource-not-found",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "Requested resource does not exist.",
  "instance": "/api/v1/tasks/01ARZ3NDEKTSV4RRFFQ69G5FAV9/req_01JZ4T59K8L7M6N5P4Q"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/internal-server-error",
  "title": "Internal Server Error",
  "status": 500,
  "detail": "An unexpected error occurred while processing the request.",
  "instance": "/api/v1/groups/req_01JZ4T8V6W5X4Y3Z2A1",
  "request_id": "req_01JZ4T8V6W5X4Y3Z2A1"
}
```

<h3 id="getmonthlytaskcalendar-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|월간 캘린더 집계.|[TaskMonthlyCalendarResponse](#schemataskmonthlycalendarresponse)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|RFC9457 problem details 응답.|[Problem](#schemaproblem)|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|RFC9457 problem details 응답.|[Problem](#schemaproblem)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|RFC9457 problem details 응답.|[Problem](#schemaproblem)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## listTaskReactions

<a id="opIdlistTaskReactions"></a>

> Code samples

```shell
# You can also use wget
curl -X GET https://api.onlogn.com/api/v1/tasks/{task_id}/reactions \
  -H 'Accept: application/json'

```

```http
GET https://api.onlogn.com/api/v1/tasks/{task_id}/reactions HTTP/1.1
Host: api.onlogn.com
Accept: application/json

```

```javascript

const headers = {
  'Accept':'application/json'
};

fetch('https://api.onlogn.com/api/v1/tasks/{task_id}/reactions',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

```ruby
require 'rest-client'
require 'json'

headers = {
  'Accept' => 'application/json'
}

result = RestClient.get 'https://api.onlogn.com/api/v1/tasks/{task_id}/reactions',
  params: {
  }, headers: headers

p JSON.parse(result)

```

```python
import requests
headers = {
  'Accept': 'application/json'
}

r = requests.get('https://api.onlogn.com/api/v1/tasks/{task_id}/reactions', headers = headers)

print(r.json())

```

```php
<?php

require 'vendor/autoload.php';

$headers = array(
    'Accept' => 'application/json',
);

$client = new \GuzzleHttp\Client();

// Define array of request body.
$request_body = array();

try {
    $response = $client->request('GET','https://api.onlogn.com/api/v1/tasks/{task_id}/reactions', array(
        'headers' => $headers,
        'json' => $request_body,
       )
    );
    print_r($response->getBody()->getContents());
 }
 catch (\GuzzleHttp\Exception\BadResponseException $e) {
    // handle exception or api errors.
    print_r($e->getMessage());
 }

 // ...

```

```java
URL obj = new URL("https://api.onlogn.com/api/v1/tasks/{task_id}/reactions");
HttpURLConnection con = (HttpURLConnection) obj.openConnection();
con.setRequestMethod("GET");
int responseCode = con.getResponseCode();
BufferedReader in = new BufferedReader(
    new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer response = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
}
in.close();
System.out.println(response.toString());

```

```go
package main

import (
       "bytes"
       "net/http"
)

func main() {

    headers := map[string][]string{
        "Accept": []string{"application/json"},
    }

    data := bytes.NewBuffer([]byte{jsonReq})
    req, err := http.NewRequest("GET", "https://api.onlogn.com/api/v1/tasks/{task_id}/reactions", data)
    req.Header = headers

    client := &http.Client{}
    resp, err := client.Do(req)
    // ...
}

```

`GET /api/v1/tasks/{task_id}/reactions`

*공개 task reaction 목록 조회*

공개 task의 reaction 집계를 반환한다.

<h3 id="listtaskreactions-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|task_id|path|string(uuid)|true|Task 식별자.|

> Example responses

> 200 Response

```json
{
  "data": [
    {
      "task_id": "123e4567-e89b-12d3-a456-426614174002",
      "emoji": ":thumbsup:",
      "count": 3,
      "requester_reacted": true
    },
    {
      "task_id": "123e4567-e89b-12d3-a456-426614174002",
      "emoji": ":rocket:",
      "count": 1,
      "requester_reacted": false
    }
  ]
}
```

> RFC9457 problem details 응답.

```json
{
  "type": "https://api.onlogn.com/problems/authentication-required",
  "title": "Authentication Required",
  "status": 401,
  "detail": "Access token is missing or expired.",
  "instance": "/api/v1/users/me/req_01JZ4T3Q8R9A2F4M7N6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/permission-denied",
  "title": "Permission Denied",
  "status": 403,
  "detail": "You cannot access another user's private resource.",
  "instance": "/api/v1/groups/01ARZ3NDEKTSV4RRFFQ69G5FAV8/req_01JZ4T4H1X2B3C4D5E6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/resource-not-found",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "Requested resource does not exist.",
  "instance": "/api/v1/tasks/01ARZ3NDEKTSV4RRFFQ69G5FAV9/req_01JZ4T59K8L7M6N5P4Q"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/internal-server-error",
  "title": "Internal Server Error",
  "status": 500,
  "detail": "An unexpected error occurred while processing the request.",
  "instance": "/api/v1/groups/req_01JZ4T8V6W5X4Y3Z2A1",
  "request_id": "req_01JZ4T8V6W5X4Y3Z2A1"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/authentication-required",
  "title": "Authentication Required",
  "status": 401,
  "detail": "Access token is missing or expired.",
  "instance": "/api/v1/users/me/req_01JZ4T3Q8R9A2F4M7N6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/permission-denied",
  "title": "Permission Denied",
  "status": 403,
  "detail": "You cannot access another user's private resource.",
  "instance": "/api/v1/groups/01ARZ3NDEKTSV4RRFFQ69G5FAV8/req_01JZ4T4H1X2B3C4D5E6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/resource-not-found",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "Requested resource does not exist.",
  "instance": "/api/v1/tasks/01ARZ3NDEKTSV4RRFFQ69G5FAV9/req_01JZ4T59K8L7M6N5P4Q"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/internal-server-error",
  "title": "Internal Server Error",
  "status": 500,
  "detail": "An unexpected error occurred while processing the request.",
  "instance": "/api/v1/groups/req_01JZ4T8V6W5X4Y3Z2A1",
  "request_id": "req_01JZ4T8V6W5X4Y3Z2A1"
}
```

<h3 id="listtaskreactions-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|Reaction 목록.|[TaskReactionListResponse](#schemataskreactionlistresponse)|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|RFC9457 problem details 응답.|[Problem](#schemaproblem)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|RFC9457 problem details 응답.|[Problem](#schemaproblem)|

<aside class="success">
This operation does not require authentication
</aside>

## toggleTaskReaction

<a id="opIdtoggleTaskReaction"></a>

> Code samples

```shell
# You can also use wget
curl -X POST https://api.onlogn.com/api/v1/tasks/{task_id}/reactions \
  -H 'Content-Type: application/json' \
  -H 'Accept: application/json' \
  -H 'Authorization: Bearer {access-token}'

```

```http
POST https://api.onlogn.com/api/v1/tasks/{task_id}/reactions HTTP/1.1
Host: api.onlogn.com
Content-Type: application/json
Accept: application/json

```

```javascript
const inputBody = '{
  "emoji": "string"
}';
const headers = {
  'Content-Type':'application/json',
  'Accept':'application/json',
  'Authorization':'Bearer {access-token}'
};

fetch('https://api.onlogn.com/api/v1/tasks/{task_id}/reactions',
{
  method: 'POST',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

```ruby
require 'rest-client'
require 'json'

headers = {
  'Content-Type' => 'application/json',
  'Accept' => 'application/json',
  'Authorization' => 'Bearer {access-token}'
}

result = RestClient.post 'https://api.onlogn.com/api/v1/tasks/{task_id}/reactions',
  params: {
  }, headers: headers

p JSON.parse(result)

```

```python
import requests
headers = {
  'Content-Type': 'application/json',
  'Accept': 'application/json',
  'Authorization': 'Bearer {access-token}'
}

r = requests.post('https://api.onlogn.com/api/v1/tasks/{task_id}/reactions', headers = headers)

print(r.json())

```

```php
<?php

require 'vendor/autoload.php';

$headers = array(
    'Content-Type' => 'application/json',
    'Accept' => 'application/json',
    'Authorization' => 'Bearer {access-token}',
);

$client = new \GuzzleHttp\Client();

// Define array of request body.
$request_body = array();

try {
    $response = $client->request('POST','https://api.onlogn.com/api/v1/tasks/{task_id}/reactions', array(
        'headers' => $headers,
        'json' => $request_body,
       )
    );
    print_r($response->getBody()->getContents());
 }
 catch (\GuzzleHttp\Exception\BadResponseException $e) {
    // handle exception or api errors.
    print_r($e->getMessage());
 }

 // ...

```

```java
URL obj = new URL("https://api.onlogn.com/api/v1/tasks/{task_id}/reactions");
HttpURLConnection con = (HttpURLConnection) obj.openConnection();
con.setRequestMethod("POST");
int responseCode = con.getResponseCode();
BufferedReader in = new BufferedReader(
    new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer response = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
}
in.close();
System.out.println(response.toString());

```

```go
package main

import (
       "bytes"
       "net/http"
)

func main() {

    headers := map[string][]string{
        "Content-Type": []string{"application/json"},
        "Accept": []string{"application/json"},
        "Authorization": []string{"Bearer {access-token}"},
    }

    data := bytes.NewBuffer([]byte{jsonReq})
    req, err := http.NewRequest("POST", "https://api.onlogn.com/api/v1/tasks/{task_id}/reactions", data)
    req.Header = headers

    client := &http.Client{}
    resp, err := client.Do(req)
    // ...
}

```

`POST /api/v1/tasks/{task_id}/reactions`

*공개 task reaction 토글*

공개 task에서 사용자별, 이모지별 reaction을 토글한다.
`(user_id, task_id, emoji)` 조합마다 논리적 reaction 행은 하나다.

> Body parameter

```json
{
  "emoji": "string"
}
```

<h3 id="toggletaskreaction-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|task_id|path|string(uuid)|true|Task 식별자.|
|body|body|[TaskReactionToggleRequest](#schemataskreactiontogglerequest)|true|none|

> Example responses

> 200 Response

```json
{
  "data": {
    "task_id": "123e4567-e89b-12d3-a456-426614174002",
    "emoji": ":thumbsup:",
    "requester_reacted": true
  }
}
```

> RFC9457 problem details 응답.

```json
{
  "type": "https://api.onlogn.com/problems/authentication-required",
  "title": "Authentication Required",
  "status": 401,
  "detail": "Access token is missing or expired.",
  "instance": "/api/v1/users/me/req_01JZ4T3Q8R9A2F4M7N6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/permission-denied",
  "title": "Permission Denied",
  "status": 403,
  "detail": "You cannot access another user's private resource.",
  "instance": "/api/v1/groups/01ARZ3NDEKTSV4RRFFQ69G5FAV8/req_01JZ4T4H1X2B3C4D5E6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/resource-not-found",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "Requested resource does not exist.",
  "instance": "/api/v1/tasks/01ARZ3NDEKTSV4RRFFQ69G5FAV9/req_01JZ4T59K8L7M6N5P4Q"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/internal-server-error",
  "title": "Internal Server Error",
  "status": 500,
  "detail": "An unexpected error occurred while processing the request.",
  "instance": "/api/v1/groups/req_01JZ4T8V6W5X4Y3Z2A1",
  "request_id": "req_01JZ4T8V6W5X4Y3Z2A1"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/authentication-required",
  "title": "Authentication Required",
  "status": 401,
  "detail": "Access token is missing or expired.",
  "instance": "/api/v1/users/me/req_01JZ4T3Q8R9A2F4M7N6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/permission-denied",
  "title": "Permission Denied",
  "status": 403,
  "detail": "You cannot access another user's private resource.",
  "instance": "/api/v1/groups/01ARZ3NDEKTSV4RRFFQ69G5FAV8/req_01JZ4T4H1X2B3C4D5E6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/resource-not-found",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "Requested resource does not exist.",
  "instance": "/api/v1/tasks/01ARZ3NDEKTSV4RRFFQ69G5FAV9/req_01JZ4T59K8L7M6N5P4Q"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/internal-server-error",
  "title": "Internal Server Error",
  "status": 500,
  "detail": "An unexpected error occurred while processing the request.",
  "instance": "/api/v1/groups/req_01JZ4T8V6W5X4Y3Z2A1",
  "request_id": "req_01JZ4T8V6W5X4Y3Z2A1"
}
```

> 422 Response

```json
{
  "type": "https://api.onlogn.com/problems/validation-failed",
  "title": "Validation Failed",
  "status": 422,
  "detail": "Request body validation failed.",
  "instance": "/api/v1/tasks/req_01JZ4T59K8L7M6N5P4Q",
  "errors": [
    {
      "field": "status",
      "reason": "must be one of: todo, in_progress, done"
    },
    {
      "field": "due_date",
      "reason": "must be a valid date in YYYY-MM-DD format"
    }
  ]
}
```

> RFC9457 problem details 응답.

```json
{
  "type": "https://api.onlogn.com/problems/authentication-required",
  "title": "Authentication Required",
  "status": 401,
  "detail": "Access token is missing or expired.",
  "instance": "/api/v1/users/me/req_01JZ4T3Q8R9A2F4M7N6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/permission-denied",
  "title": "Permission Denied",
  "status": 403,
  "detail": "You cannot access another user's private resource.",
  "instance": "/api/v1/groups/01ARZ3NDEKTSV4RRFFQ69G5FAV8/req_01JZ4T4H1X2B3C4D5E6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/resource-not-found",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "Requested resource does not exist.",
  "instance": "/api/v1/tasks/01ARZ3NDEKTSV4RRFFQ69G5FAV9/req_01JZ4T59K8L7M6N5P4Q"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/internal-server-error",
  "title": "Internal Server Error",
  "status": 500,
  "detail": "An unexpected error occurred while processing the request.",
  "instance": "/api/v1/groups/req_01JZ4T8V6W5X4Y3Z2A1",
  "request_id": "req_01JZ4T8V6W5X4Y3Z2A1"
}
```

<h3 id="toggletaskreaction-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|Reaction 상태 토글 완료.|[TaskReactionMutationResponse](#schemataskreactionmutationresponse)|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|RFC9457 problem details 응답.|[Problem](#schemaproblem)|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|RFC9457 problem details 응답.|[Problem](#schemaproblem)|
|422|[Unprocessable Entity](https://tools.ietf.org/html/rfc2518#section-10.3)|RFC9457 validation problem details 응답.|[ValidationProblem](#schemavalidationproblem)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|RFC9457 problem details 응답.|[Problem](#schemaproblem)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## removeTaskReaction

<a id="opIdremoveTaskReaction"></a>

> Code samples

```shell
# You can also use wget
curl -X DELETE https://api.onlogn.com/api/v1/tasks/{task_id}/reactions?emoji=string \
  -H 'Accept: application/json' \
  -H 'Authorization: Bearer {access-token}'

```

```http
DELETE https://api.onlogn.com/api/v1/tasks/{task_id}/reactions?emoji=string HTTP/1.1
Host: api.onlogn.com
Accept: application/json

```

```javascript

const headers = {
  'Accept':'application/json',
  'Authorization':'Bearer {access-token}'
};

fetch('https://api.onlogn.com/api/v1/tasks/{task_id}/reactions?emoji=string',
{
  method: 'DELETE',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

```ruby
require 'rest-client'
require 'json'

headers = {
  'Accept' => 'application/json',
  'Authorization' => 'Bearer {access-token}'
}

result = RestClient.delete 'https://api.onlogn.com/api/v1/tasks/{task_id}/reactions',
  params: {
  'emoji' => 'string'
}, headers: headers

p JSON.parse(result)

```

```python
import requests
headers = {
  'Accept': 'application/json',
  'Authorization': 'Bearer {access-token}'
}

r = requests.delete('https://api.onlogn.com/api/v1/tasks/{task_id}/reactions', params={
  'emoji': 'string'
}, headers = headers)

print(r.json())

```

```php
<?php

require 'vendor/autoload.php';

$headers = array(
    'Accept' => 'application/json',
    'Authorization' => 'Bearer {access-token}',
);

$client = new \GuzzleHttp\Client();

// Define array of request body.
$request_body = array();

try {
    $response = $client->request('DELETE','https://api.onlogn.com/api/v1/tasks/{task_id}/reactions', array(
        'headers' => $headers,
        'json' => $request_body,
       )
    );
    print_r($response->getBody()->getContents());
 }
 catch (\GuzzleHttp\Exception\BadResponseException $e) {
    // handle exception or api errors.
    print_r($e->getMessage());
 }

 // ...

```

```java
URL obj = new URL("https://api.onlogn.com/api/v1/tasks/{task_id}/reactions?emoji=string");
HttpURLConnection con = (HttpURLConnection) obj.openConnection();
con.setRequestMethod("DELETE");
int responseCode = con.getResponseCode();
BufferedReader in = new BufferedReader(
    new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer response = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
}
in.close();
System.out.println(response.toString());

```

```go
package main

import (
       "bytes"
       "net/http"
)

func main() {

    headers := map[string][]string{
        "Accept": []string{"application/json"},
        "Authorization": []string{"Bearer {access-token}"},
    }

    data := bytes.NewBuffer([]byte{jsonReq})
    req, err := http.NewRequest("DELETE", "https://api.onlogn.com/api/v1/tasks/{task_id}/reactions", data)
    req.Header = headers

    client := &http.Client{}
    resp, err := client.Do(req)
    // ...
}

```

`DELETE /api/v1/tasks/{task_id}/reactions`

*공개 task reaction 제거*

공개 task에서 현재 사용자의 특정 emoji reaction을 제거한다.

<h3 id="removetaskreaction-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|task_id|path|string(uuid)|true|Task 식별자.|
|emoji|query|string|true|none|

> Example responses

> 200 Response

```json
{
  "data": {
    "task_id": "123e4567-e89b-12d3-a456-426614174002",
    "emoji": ":thumbsup:",
    "requester_reacted": true
  }
}
```

> RFC9457 problem details 응답.

```json
{
  "type": "https://api.onlogn.com/problems/authentication-required",
  "title": "Authentication Required",
  "status": 401,
  "detail": "Access token is missing or expired.",
  "instance": "/api/v1/users/me/req_01JZ4T3Q8R9A2F4M7N6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/permission-denied",
  "title": "Permission Denied",
  "status": 403,
  "detail": "You cannot access another user's private resource.",
  "instance": "/api/v1/groups/01ARZ3NDEKTSV4RRFFQ69G5FAV8/req_01JZ4T4H1X2B3C4D5E6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/resource-not-found",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "Requested resource does not exist.",
  "instance": "/api/v1/tasks/01ARZ3NDEKTSV4RRFFQ69G5FAV9/req_01JZ4T59K8L7M6N5P4Q"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/internal-server-error",
  "title": "Internal Server Error",
  "status": 500,
  "detail": "An unexpected error occurred while processing the request.",
  "instance": "/api/v1/groups/req_01JZ4T8V6W5X4Y3Z2A1",
  "request_id": "req_01JZ4T8V6W5X4Y3Z2A1"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/authentication-required",
  "title": "Authentication Required",
  "status": 401,
  "detail": "Access token is missing or expired.",
  "instance": "/api/v1/users/me/req_01JZ4T3Q8R9A2F4M7N6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/permission-denied",
  "title": "Permission Denied",
  "status": 403,
  "detail": "You cannot access another user's private resource.",
  "instance": "/api/v1/groups/01ARZ3NDEKTSV4RRFFQ69G5FAV8/req_01JZ4T4H1X2B3C4D5E6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/resource-not-found",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "Requested resource does not exist.",
  "instance": "/api/v1/tasks/01ARZ3NDEKTSV4RRFFQ69G5FAV9/req_01JZ4T59K8L7M6N5P4Q"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/internal-server-error",
  "title": "Internal Server Error",
  "status": 500,
  "detail": "An unexpected error occurred while processing the request.",
  "instance": "/api/v1/groups/req_01JZ4T8V6W5X4Y3Z2A1",
  "request_id": "req_01JZ4T8V6W5X4Y3Z2A1"
}
```

> 422 Response

```json
{
  "type": "https://api.onlogn.com/problems/validation-failed",
  "title": "Validation Failed",
  "status": 422,
  "detail": "Request body validation failed.",
  "instance": "/api/v1/tasks/req_01JZ4T59K8L7M6N5P4Q",
  "errors": [
    {
      "field": "status",
      "reason": "must be one of: todo, in_progress, done"
    },
    {
      "field": "due_date",
      "reason": "must be a valid date in YYYY-MM-DD format"
    }
  ]
}
```

> RFC9457 problem details 응답.

```json
{
  "type": "https://api.onlogn.com/problems/authentication-required",
  "title": "Authentication Required",
  "status": 401,
  "detail": "Access token is missing or expired.",
  "instance": "/api/v1/users/me/req_01JZ4T3Q8R9A2F4M7N6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/permission-denied",
  "title": "Permission Denied",
  "status": 403,
  "detail": "You cannot access another user's private resource.",
  "instance": "/api/v1/groups/01ARZ3NDEKTSV4RRFFQ69G5FAV8/req_01JZ4T4H1X2B3C4D5E6"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/resource-not-found",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "Requested resource does not exist.",
  "instance": "/api/v1/tasks/01ARZ3NDEKTSV4RRFFQ69G5FAV9/req_01JZ4T59K8L7M6N5P4Q"
}
```

```json
{
  "type": "https://api.onlogn.com/problems/internal-server-error",
  "title": "Internal Server Error",
  "status": 500,
  "detail": "An unexpected error occurred while processing the request.",
  "instance": "/api/v1/groups/req_01JZ4T8V6W5X4Y3Z2A1",
  "request_id": "req_01JZ4T8V6W5X4Y3Z2A1"
}
```

<h3 id="removetaskreaction-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|Reaction 제거 완료.|[TaskReactionMutationResponse](#schemataskreactionmutationresponse)|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|RFC9457 problem details 응답.|[Problem](#schemaproblem)|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|RFC9457 problem details 응답.|[Problem](#schemaproblem)|
|422|[Unprocessable Entity](https://tools.ietf.org/html/rfc2518#section-10.3)|RFC9457 validation problem details 응답.|[ValidationProblem](#schemavalidationproblem)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|RFC9457 problem details 응답.|[Problem](#schemaproblem)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

# Schemas

<h2 id="tocS_TaskStatus">TaskStatus</h2>
<!-- backwards compatibility -->
<a id="schemataskstatus"></a>
<a id="schema_TaskStatus"></a>
<a id="tocStaskstatus"></a>
<a id="tocstaskstatus"></a>

```json
"todo"

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|*anonymous*|string|false|none|none|

#### Enumerated Values

|Property|Value|
|---|---|
|*anonymous*|todo|
|*anonymous*|in_progress|
|*anonymous*|done|

<h2 id="tocS_TaskVisibility">TaskVisibility</h2>
<!-- backwards compatibility -->
<a id="schemataskvisibility"></a>
<a id="schema_TaskVisibility"></a>
<a id="tocStaskvisibility"></a>
<a id="tocstaskvisibility"></a>

```json
"private"

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|*anonymous*|string|false|none|none|

#### Enumerated Values

|Property|Value|
|---|---|
|*anonymous*|private|
|*anonymous*|public|

<h2 id="tocS_AuthGithubExchangeRequestData">AuthGithubExchangeRequestData</h2>
<!-- backwards compatibility -->
<a id="schemaauthgithubexchangerequestdata"></a>
<a id="schema_AuthGithubExchangeRequestData"></a>
<a id="tocSauthgithubexchangerequestdata"></a>
<a id="tocsauthgithubexchangerequestdata"></a>

```json
{
  "code": "gh_oauth_code_123"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|string|true|none|GitHub OAuth가 발급한 authorization code.|

<h2 id="tocS_AuthGithubExchangeRequestEnvelope">AuthGithubExchangeRequestEnvelope</h2>
<!-- backwards compatibility -->
<a id="schemaauthgithubexchangerequestenvelope"></a>
<a id="schema_AuthGithubExchangeRequestEnvelope"></a>
<a id="tocSauthgithubexchangerequestenvelope"></a>
<a id="tocsauthgithubexchangerequestenvelope"></a>

```json
{
  "data": {
    "code": "gh_oauth_code_123"
  }
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|data|[AuthGithubExchangeRequestData](#schemaauthgithubexchangerequestdata)|true|none|none|

<h2 id="tocS_AuthRefreshRequestData">AuthRefreshRequestData</h2>
<!-- backwards compatibility -->
<a id="schemaauthrefreshrequestdata"></a>
<a id="schema_AuthRefreshRequestData"></a>
<a id="tocSauthrefreshrequestdata"></a>
<a id="tocsauthrefreshrequestdata"></a>

```json
{
  "refresh_token": "rf_01JZ4T3Q8R9A2F4M7N6"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|refresh_token|string|true|none|현재 클라이언트가 보유한 refresh token.|

<h2 id="tocS_AuthRefreshRequestEnvelope">AuthRefreshRequestEnvelope</h2>
<!-- backwards compatibility -->
<a id="schemaauthrefreshrequestenvelope"></a>
<a id="schema_AuthRefreshRequestEnvelope"></a>
<a id="tocSauthrefreshrequestenvelope"></a>
<a id="tocsauthrefreshrequestenvelope"></a>

```json
{
  "data": {
    "refresh_token": "rf_01JZ4T3Q8R9A2F4M7N6"
  }
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|data|[AuthRefreshRequestData](#schemaauthrefreshrequestdata)|true|none|none|

<h2 id="tocS_AuthLogoutRequestData">AuthLogoutRequestData</h2>
<!-- backwards compatibility -->
<a id="schemaauthlogoutrequestdata"></a>
<a id="schema_AuthLogoutRequestData"></a>
<a id="tocSauthlogoutrequestdata"></a>
<a id="tocsauthlogoutrequestdata"></a>

```json
{
  "refresh_token": "rf_01JZ4T3Q8R9A2F4M7N6"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|refresh_token|string|true|none|현재 디바이스 세션 폐기를 위한 refresh token.|

<h2 id="tocS_AuthLogoutRequestEnvelope">AuthLogoutRequestEnvelope</h2>
<!-- backwards compatibility -->
<a id="schemaauthlogoutrequestenvelope"></a>
<a id="schema_AuthLogoutRequestEnvelope"></a>
<a id="tocSauthlogoutrequestenvelope"></a>
<a id="tocsauthlogoutrequestenvelope"></a>

```json
{
  "data": {
    "refresh_token": "rf_01JZ4T3Q8R9A2F4M7N6"
  }
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|data|[AuthLogoutRequestData](#schemaauthlogoutrequestdata)|true|none|none|

<h2 id="tocS_AuthTokenPayload">AuthTokenPayload</h2>
<!-- backwards compatibility -->
<a id="schemaauthtokenpayload"></a>
<a id="schema_AuthTokenPayload"></a>
<a id="tocSauthtokenpayload"></a>
<a id="tocsauthtokenpayload"></a>

```json
{
  "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refresh_token": "rf_01JZ4T3Q8R9A2F4M7N6",
  "token_type": "Bearer",
  "expires_in": 900,
  "refresh_expires_in": 1209600
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|access_token|string|true|none|보호된 API 작업용 access token.|
|refresh_token|string|true|none|회전용 1회 사용 refresh token.|
|token_type|string|true|none|Authorization token 유형.|
|expires_in|integer|true|none|access token TTL(초).|
|refresh_expires_in|integer|true|none|refresh token TTL(초).|

#### Enumerated Values

|Property|Value|
|---|---|
|token_type|Bearer|

<h2 id="tocS_AuthTokenSuccessEnvelope">AuthTokenSuccessEnvelope</h2>
<!-- backwards compatibility -->
<a id="schemaauthtokensuccessenvelope"></a>
<a id="schema_AuthTokenSuccessEnvelope"></a>
<a id="tocSauthtokensuccessenvelope"></a>
<a id="tocsauthtokensuccessenvelope"></a>

```json
{
  "data": {
    "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.example",
    "refresh_token": "rf_01JZ4T3Q8R9A2F4M7N6",
    "token_type": "Bearer",
    "expires_in": 900,
    "refresh_expires_in": 1209600
  }
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|data|[AuthTokenPayload](#schemaauthtokenpayload)|true|none|none|

<h2 id="tocS_Problem">Problem</h2>
<!-- backwards compatibility -->
<a id="schemaproblem"></a>
<a id="schema_Problem"></a>
<a id="tocSproblem"></a>
<a id="tocsproblem"></a>

```json
{
  "type": "https://api.onlogn.com/problems/resource-not-found",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "Requested resource does not exist.",
  "instance": "/api/v1/tasks/req_01JZ4T3Q8R9A2F4M7N6",
  "request_id": "string"
}

```

RFC9457 problem details 객체.

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|type|string(uri)|true|none|안정적인 problem type URI.|
|title|string|true|none|problem type의 짧고 사람이 읽을 수 있는 요약.|
|status|integer|true|none|HTTP 상태 코드.|
|detail|string|true|none|이 발생 건에 대한 사람이 읽을 수 있는 설명.|
|instance|string|true|none|해당 problem 발생 건을 가리키는 URI 참조.|
|request_id|string|false|none|지원 및 관측을 위한 추적 식별자.|

<h2 id="tocS_ValidationErrorItem">ValidationErrorItem</h2>
<!-- backwards compatibility -->
<a id="schemavalidationerroritem"></a>
<a id="schema_ValidationErrorItem"></a>
<a id="tocSvalidationerroritem"></a>
<a id="tocsvalidationerroritem"></a>

```json
{
  "field": "status",
  "reason": "must be one of: todo, in_progress, done"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|field|string|true|none|유효하지 않은 요청 필드 이름.|
|reason|string|true|none|검증 실패 사유.|

<h2 id="tocS_ValidationProblem">ValidationProblem</h2>
<!-- backwards compatibility -->
<a id="schemavalidationproblem"></a>
<a id="schema_ValidationProblem"></a>
<a id="tocSvalidationproblem"></a>
<a id="tocsvalidationproblem"></a>

```json
{
  "type": "https://api.onlogn.com/problems/resource-not-found",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "Requested resource does not exist.",
  "instance": "/api/v1/tasks/req_01JZ4T3Q8R9A2F4M7N6",
  "request_id": "string",
  "errors": [
    {
      "field": "status",
      "reason": "must be one of: todo, in_progress, done"
    }
  ]
}

```

### Properties

allOf

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|*anonymous*|[Problem](#schemaproblem)|false|none|RFC9457 problem details 객체.|

and

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|*anonymous*|object|false|none|none|
|» errors|[[ValidationErrorItem](#schemavalidationerroritem)]|false|none|none|

<h2 id="tocS_PaginationMeta">PaginationMeta</h2>
<!-- backwards compatibility -->
<a id="schemapaginationmeta"></a>
<a id="schema_PaginationMeta"></a>
<a id="tocSpaginationmeta"></a>
<a id="tocspaginationmeta"></a>

```json
{
  "offset": 0,
  "limit": 20,
  "total": 125,
  "sort": "created_at desc, id desc"
}

```

목록 응답용 공통 페이지네이션 메타데이터.

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|offset|integer|true|none|none|
|limit|integer|true|none|none|
|total|integer|true|none|none|
|sort|string|true|none|결정적인 정렬 표현식.|

<h2 id="tocS_ListMeta">ListMeta</h2>
<!-- backwards compatibility -->
<a id="schemalistmeta"></a>
<a id="schema_ListMeta"></a>
<a id="tocSlistmeta"></a>
<a id="tocslistmeta"></a>

```json
{
  "pagination": {
    "offset": 0,
    "limit": 20,
    "total": 125,
    "sort": "created_at desc, id desc"
  }
}

```

목록 응답을 위한 공통 `meta` 객체 기준선.

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|pagination|[PaginationMeta](#schemapaginationmeta)|true|none|목록 응답용 공통 페이지네이션 메타데이터.|

<h2 id="tocS_DataMetaEnvelope">DataMetaEnvelope</h2>
<!-- backwards compatibility -->
<a id="schemadatametaenvelope"></a>
<a id="schema_DataMetaEnvelope"></a>
<a id="tocSdatametaenvelope"></a>
<a id="tocsdatametaenvelope"></a>

```json
{
  "data": null,
  "meta": {
    "pagination": {
      "offset": 0,
      "limit": 20,
      "total": 125,
      "sort": "created_at desc, id desc"
    }
  }
}

```

공통 성공 envelope 기준선(`data` + optional `meta`).

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|data|any|true|none|작업 payload, 구체 스키마는 엔드포인트별로 정의된다.|
|meta|[ListMeta](#schemalistmeta)|false|none|목록/집계 응답용 선택 메타데이터.|

<h2 id="tocS_UserMeProfile">UserMeProfile</h2>
<!-- backwards compatibility -->
<a id="schemausermeprofile"></a>
<a id="schema_UserMeProfile"></a>
<a id="tocSusermeprofile"></a>
<a id="tocsusermeprofile"></a>

```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "profile_slug": "johndoe",
  "email": "user@example.com",
  "display_name": "John Doe",
  "avatar_url": "https://cdn.onlogn.com/avatars/user.png",
  "timezone": "UTC",
  "created_at": "2025-01-01T00:00:00Z",
  "updated_at": "2025-01-01T01:00:00Z"
}

```

`/api/v1/users/me`용 인증 사용자 payload.

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|string(uuid)|true|none|none|
|profile_slug|string|true|none|none|
|email|string(email)¦null|true|none|none|
|display_name|string|true|none|none|
|avatar_url|string(uri)¦null|true|none|none|
|timezone|string|true|none|none|
|created_at|string(date-time)|true|none|none|
|updated_at|string(date-time)|true|none|none|

<h2 id="tocS_ProfilePublic">ProfilePublic</h2>
<!-- backwards compatibility -->
<a id="schemaprofilepublic"></a>
<a id="schema_ProfilePublic"></a>
<a id="tocSprofilepublic"></a>
<a id="tocsprofilepublic"></a>

```json
{
  "profile_slug": "johndoe",
  "display_name": "John Doe",
  "avatar_url": "https://cdn.onlogn.com/avatars/user.png",
  "bio": "Product designer focused on clear systems.",
  "timezone": "UTC",
  "created_at": "2025-01-01T00:00:00Z",
  "stats_summary": {
    "total_tasks": 12,
    "done_tasks": 8,
    "in_progress_tasks": 3,
    "todo_tasks": 1
  }
}

```

`/api/v1/profiles/{slug}`에서 공개되는 profile 필드.

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|profile_slug|string|true|none|none|
|display_name|string|true|none|none|
|avatar_url|string(uri)¦null|true|none|none|
|bio|string¦null|true|none|none|
|timezone|string|true|none|none|
|created_at|string(date-time)|true|none|none|
|stats_summary|[ProfileStatsSummary](#schemaprofilestatssummary)|true|none|공개 profile 응답에 포함되는 집계 task 통계.|

<h2 id="tocS_ProfileStatsSummary">ProfileStatsSummary</h2>
<!-- backwards compatibility -->
<a id="schemaprofilestatssummary"></a>
<a id="schema_ProfileStatsSummary"></a>
<a id="tocSprofilestatssummary"></a>
<a id="tocsprofilestatssummary"></a>

```json
{
  "total_tasks": 12,
  "done_tasks": 8,
  "in_progress_tasks": 3,
  "todo_tasks": 1
}

```

공개 profile 응답에 포함되는 집계 task 통계.

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|total_tasks|integer|true|none|none|
|done_tasks|integer|true|none|none|
|in_progress_tasks|integer|true|none|none|
|todo_tasks|integer|true|none|none|

<h2 id="tocS_Group">Group</h2>
<!-- backwards compatibility -->
<a id="schemagroup"></a>
<a id="schema_Group"></a>
<a id="tocSgroup"></a>
<a id="tocsgroup"></a>

```json
{
  "id": "123e4567-e89b-12d3-a456-426614174001",
  "owner_user_id": "123e4567-e89b-12d3-a456-426614174000",
  "visibility": "private",
  "description": "Weekly planning workspace.",
  "color": "#4f46e5",
  "icon": "folder",
  "created_at": "2026-02-01T09:00:00Z",
  "updated_at": "2026-02-01T09:15:00Z"
}

```

Group 리소스 표현.

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|string(uuid)|true|none|none|
|owner_user_id|string(uuid)|true|none|none|
|visibility|string|true|none|group의 visibility 정책 (`private|public`).|
|description|string¦null|false|none|선택 설명 텍스트.|
|color|string¦null|false|none|선택 UI 강조 색상 토큰.|
|icon|string¦null|false|none|선택 UI 아이콘 토큰.|
|created_at|string(date-time)|true|none|none|
|updated_at|string(date-time)|true|none|none|

#### Enumerated Values

|Property|Value|
|---|---|
|visibility|private|
|visibility|public|

<h2 id="tocS_GroupCreateRequest">GroupCreateRequest</h2>
<!-- backwards compatibility -->
<a id="schemagroupcreaterequest"></a>
<a id="schema_GroupCreateRequest"></a>
<a id="tocSgroupcreaterequest"></a>
<a id="tocsgroupcreaterequest"></a>

```json
{
  "visibility": "private",
  "description": "New group for high priority work.",
  "color": "#f97316",
  "icon": "star"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|visibility|string|true|none|group visibility 정책 (`private|public`).|
|description|string¦null|false|none|선택 설명 텍스트.|
|color|string¦null|false|none|선택 UI 강조 색상 토큰.|
|icon|string¦null|false|none|선택 UI 아이콘 토큰.|

#### Enumerated Values

|Property|Value|
|---|---|
|visibility|private|
|visibility|public|

<h2 id="tocS_GroupUpdateRequest">GroupUpdateRequest</h2>
<!-- backwards compatibility -->
<a id="schemagroupupdaterequest"></a>
<a id="schema_GroupUpdateRequest"></a>
<a id="tocSgroupupdaterequest"></a>
<a id="tocsgroupupdaterequest"></a>

```json
{
  "visibility": "private",
  "description": "Updated notes for this group.",
  "color": "#0ea5e9",
  "icon": "tag"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|visibility|string|false|none|group visibility 정책 (`private|public`).|
|description|string¦null|false|none|선택 설명 텍스트.|
|color|string¦null|false|none|선택 UI 강조 색상 토큰.|
|icon|string¦null|false|none|선택 UI 아이콘 토큰.|

#### Enumerated Values

|Property|Value|
|---|---|
|visibility|private|
|visibility|public|

<h2 id="tocS_GroupResponse">GroupResponse</h2>
<!-- backwards compatibility -->
<a id="schemagroupresponse"></a>
<a id="schema_GroupResponse"></a>
<a id="tocSgroupresponse"></a>
<a id="tocsgroupresponse"></a>

```json
{
  "data": {
    "id": "123e4567-e89b-12d3-a456-426614174001",
    "owner_user_id": "123e4567-e89b-12d3-a456-426614174000",
    "visibility": "private",
    "description": "Weekly planning workspace.",
    "color": "#4f46e5",
    "icon": "folder",
    "created_at": "2026-02-01T09:00:00Z",
    "updated_at": "2026-02-01T09:15:00Z"
  }
}

```

### Properties

allOf

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|*anonymous*|[DataMetaEnvelope](#schemadatametaenvelope)|false|none|공통 성공 envelope 기준선(`data` + optional `meta`).|

and

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|*anonymous*|object|false|none|none|
|» data|[Group](#schemagroup)|false|none|Group 리소스 표현.|

<h2 id="tocS_GroupListResponse">GroupListResponse</h2>
<!-- backwards compatibility -->
<a id="schemagrouplistresponse"></a>
<a id="schema_GroupListResponse"></a>
<a id="tocSgrouplistresponse"></a>
<a id="tocsgrouplistresponse"></a>

```json
{
  "data": [
    {
      "id": "123e4567-e89b-12d3-a456-426614174001",
      "owner_user_id": "123e4567-e89b-12d3-a456-426614174000",
      "visibility": "private",
      "description": "Weekly planning workspace.",
      "color": "#4f46e5",
      "icon": "folder",
      "created_at": "2026-02-01T09:00:00Z",
      "updated_at": "2026-02-01T09:15:00Z"
    }
  ],
  "meta": {
    "pagination": {
      "offset": 0,
      "limit": 20,
      "total": 1,
      "sort": "created_at desc, id desc"
    }
  }
}

```

### Properties

allOf

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|*anonymous*|[DataMetaEnvelope](#schemadatametaenvelope)|false|none|공통 성공 envelope 기준선(`data` + optional `meta`).|

and

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|*anonymous*|object|false|none|none|
|» data|[[Group](#schemagroup)]|false|none|[Group 리소스 표현.]|
|» meta|[ListMeta](#schemalistmeta)|false|none|목록 응답을 위한 공통 `meta` 객체 기준선.|

<h2 id="tocS_Task">Task</h2>
<!-- backwards compatibility -->
<a id="schematask"></a>
<a id="schema_Task"></a>
<a id="tocStask"></a>
<a id="tocstask"></a>

```json
{
  "id": "497f6eca-6276-4993-bfeb-53cbbbba6f08",
  "owner_user_id": "65139110-7c3c-4777-b692-80c218be3b9d",
  "group_id": null,
  "title": "string",
  "status": "todo",
  "visibility": "private",
  "created_at": "2019-08-24T14:15:22Z",
  "updated_at": "2019-08-24T14:15:22Z",
  "due_date": null,
  "start_time": null,
  "end_time": null,
  "tags": [],
  "reference_links": []
}

```

Task 리소스 표현.

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|string(uuid)|true|none|none|
|owner_user_id|string(uuid)|true|none|none|
|group_id|any|true|none|none|

oneOf

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|» *anonymous*|string(uuid)|false|none|none|

xor

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|» *anonymous*|null|false|none|none|

continued

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|title|string|true|none|none|
|status|[TaskStatus](#schemataskstatus)|true|none|none|
|visibility|[TaskVisibility](#schemataskvisibility)|true|none|none|
|created_at|string(date-time)|true|none|none|
|updated_at|string(date-time)|true|none|none|
|due_date|any|true|none|none|

oneOf

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|» *anonymous*|string(date)|false|none|none|

xor

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|» *anonymous*|null|false|none|none|

continued

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|start_time|any|true|none|none|

oneOf

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|» *anonymous*|string(date-time)|false|none|none|

xor

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|» *anonymous*|null|false|none|none|

continued

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|end_time|any|true|none|none|

oneOf

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|» *anonymous*|string(date-time)|false|none|none|

xor

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|» *anonymous*|null|false|none|none|

continued

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|tags|[string]|true|none|none|
|reference_links|[string]|true|none|none|

<h2 id="tocS_TaskCreateRequest">TaskCreateRequest</h2>
<!-- backwards compatibility -->
<a id="schemataskcreaterequest"></a>
<a id="schema_TaskCreateRequest"></a>
<a id="tocStaskcreaterequest"></a>
<a id="tocstaskcreaterequest"></a>

```json
{
  "group_id": "306db4e0-7449-4501-b76f-075576fe2d8f",
  "title": "string",
  "status": "todo",
  "visibility": "private",
  "due_date": "2019-08-24",
  "start_time": "2019-08-24T14:15:22Z",
  "end_time": "2019-08-24T14:15:22Z",
  "tags": [
    "string"
  ],
  "reference_links": [
    "http://example.com"
  ]
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|group_id|any|false|none|none|

oneOf

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|» *anonymous*|string(uuid)|false|none|none|

xor

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|» *anonymous*|null|false|none|none|

continued

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|title|string|true|none|none|
|status|[TaskStatus](#schemataskstatus)|false|none|none|
|visibility|[TaskVisibility](#schemataskvisibility)|false|none|none|
|due_date|any|false|none|none|

oneOf

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|» *anonymous*|string(date)|false|none|none|

xor

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|» *anonymous*|null|false|none|none|

continued

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|start_time|any|false|none|none|

oneOf

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|» *anonymous*|string(date-time)|false|none|none|

xor

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|» *anonymous*|null|false|none|none|

continued

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|end_time|any|false|none|none|

oneOf

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|» *anonymous*|string(date-time)|false|none|none|

xor

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|» *anonymous*|null|false|none|none|

continued

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|tags|[string]|false|none|none|
|reference_links|[string]|false|none|none|

<h2 id="tocS_TaskUpdateRequest">TaskUpdateRequest</h2>
<!-- backwards compatibility -->
<a id="schemataskupdaterequest"></a>
<a id="schema_TaskUpdateRequest"></a>
<a id="tocStaskupdaterequest"></a>
<a id="tocstaskupdaterequest"></a>

```json
{
  "group_id": "306db4e0-7449-4501-b76f-075576fe2d8f",
  "title": "string",
  "status": "todo",
  "visibility": "private",
  "due_date": "2019-08-24",
  "start_time": "2019-08-24T14:15:22Z",
  "end_time": "2019-08-24T14:15:22Z",
  "tags": [
    "string"
  ],
  "reference_links": [
    "http://example.com"
  ]
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|group_id|any|false|none|none|

oneOf

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|» *anonymous*|string(uuid)|false|none|none|

xor

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|» *anonymous*|null|false|none|none|

continued

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|title|string|false|none|none|
|status|[TaskStatus](#schemataskstatus)|false|none|none|
|visibility|[TaskVisibility](#schemataskvisibility)|false|none|none|
|due_date|any|false|none|none|

oneOf

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|» *anonymous*|string(date)|false|none|none|

xor

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|» *anonymous*|null|false|none|none|

continued

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|start_time|any|false|none|none|

oneOf

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|» *anonymous*|string(date-time)|false|none|none|

xor

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|» *anonymous*|null|false|none|none|

continued

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|end_time|any|false|none|none|

oneOf

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|» *anonymous*|string(date-time)|false|none|none|

xor

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|» *anonymous*|null|false|none|none|

continued

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|tags|[string]|false|none|none|
|reference_links|[string]|false|none|none|

<h2 id="tocS_TaskResponse">TaskResponse</h2>
<!-- backwards compatibility -->
<a id="schemataskresponse"></a>
<a id="schema_TaskResponse"></a>
<a id="tocStaskresponse"></a>
<a id="tocstaskresponse"></a>

```json
{
  "data": {
    "id": "123e4567-e89b-12d3-a456-426614174002",
    "owner_user_id": "123e4567-e89b-12d3-a456-426614174000",
    "group_id": "123e4567-e89b-12d3-a456-426614174001",
    "title": "Prepare sprint handoff",
    "status": "in_progress",
    "visibility": "private",
    "created_at": "2026-02-10T09:00:00Z",
    "updated_at": "2026-02-10T10:30:00Z",
    "due_date": "2026-02-12",
    "start_time": "2026-02-10T09:00:00Z",
    "end_time": null,
    "tags": [
      "planning",
      "backend"
    ],
    "reference_links": [
      "https://docs.onlogn.com/specs/sprint-handoff"
    ]
  }
}

```

### Properties

allOf

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|*anonymous*|[DataMetaEnvelope](#schemadatametaenvelope)|false|none|공통 성공 envelope 기준선(`data` + optional `meta`).|

and

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|*anonymous*|object|false|none|none|
|» data|[Task](#schematask)|false|none|Task 리소스 표현.|

<h2 id="tocS_TaskListResponse">TaskListResponse</h2>
<!-- backwards compatibility -->
<a id="schematasklistresponse"></a>
<a id="schema_TaskListResponse"></a>
<a id="tocStasklistresponse"></a>
<a id="tocstasklistresponse"></a>

```json
{
  "data": [
    {
      "id": "123e4567-e89b-12d3-a456-426614174002",
      "owner_user_id": "123e4567-e89b-12d3-a456-426614174000",
      "group_id": "123e4567-e89b-12d3-a456-426614174001",
      "title": "Prepare sprint handoff",
      "status": "in_progress",
      "visibility": "private",
      "created_at": "2026-02-10T09:00:00Z",
      "updated_at": "2026-02-10T10:30:00Z",
      "due_date": "2026-02-12",
      "start_time": "2026-02-10T09:00:00Z",
      "end_time": null,
      "tags": [
        "planning",
        "backend"
      ],
      "reference_links": [
        "https://docs.onlogn.com/specs/sprint-handoff"
      ]
    }
  ],
  "meta": {
    "pagination": {
      "offset": 0,
      "limit": 20,
      "total": 1,
      "sort": "created_at desc, id desc"
    }
  }
}

```

### Properties

allOf

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|*anonymous*|[DataMetaEnvelope](#schemadatametaenvelope)|false|none|공통 성공 envelope 기준선(`data` + optional `meta`).|

and

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|*anonymous*|object|false|none|none|
|» data|[[Task](#schematask)]|false|none|[Task 리소스 표현.]|
|» meta|[ListMeta](#schemalistmeta)|false|none|목록 응답을 위한 공통 `meta` 객체 기준선.|

<h2 id="tocS_TaskMonthlyCalendarDay">TaskMonthlyCalendarDay</h2>
<!-- backwards compatibility -->
<a id="schemataskmonthlycalendarday"></a>
<a id="schema_TaskMonthlyCalendarDay"></a>
<a id="tocStaskmonthlycalendarday"></a>
<a id="tocstaskmonthlycalendarday"></a>

```json
{
  "date": "2019-08-24",
  "planned_count": 0,
  "done_count": 0,
  "in_progress_count": 0,
  "completion_rate": 0
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|date|string(date)|true|none|none|
|planned_count|integer|true|none|none|
|done_count|integer|true|none|none|
|in_progress_count|integer|true|none|none|
|completion_rate|number|true|none|일일 완료율 백분율 범위 0..100.|

<h2 id="tocS_TaskMonthlyCalendarMeta">TaskMonthlyCalendarMeta</h2>
<!-- backwards compatibility -->
<a id="schemataskmonthlycalendarmeta"></a>
<a id="schema_TaskMonthlyCalendarMeta"></a>
<a id="tocStaskmonthlycalendarmeta"></a>
<a id="tocstaskmonthlycalendarmeta"></a>

```json
{
  "year": 1970,
  "month": 1,
  "timezone": "string"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|year|integer|true|none|none|
|month|integer|true|none|none|
|timezone|string|true|none|none|

<h2 id="tocS_TaskMonthlyCalendarResponse">TaskMonthlyCalendarResponse</h2>
<!-- backwards compatibility -->
<a id="schemataskmonthlycalendarresponse"></a>
<a id="schema_TaskMonthlyCalendarResponse"></a>
<a id="tocStaskmonthlycalendarresponse"></a>
<a id="tocstaskmonthlycalendarresponse"></a>

```json
{
  "data": [
    {
      "date": "2026-02-01",
      "planned_count": 2,
      "done_count": 1,
      "in_progress_count": 1,
      "completion_rate": 50
    },
    {
      "date": "2026-02-02",
      "planned_count": 0,
      "done_count": 0,
      "in_progress_count": 0,
      "completion_rate": 0
    }
  ],
  "meta": {
    "year": 2026,
    "month": 2,
    "timezone": "Asia/Seoul"
  }
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|data|[[TaskMonthlyCalendarDay](#schemataskmonthlycalendarday)]|true|none|none|
|meta|[TaskMonthlyCalendarMeta](#schemataskmonthlycalendarmeta)|true|none|none|

<h2 id="tocS_TaskReaction">TaskReaction</h2>
<!-- backwards compatibility -->
<a id="schemataskreaction"></a>
<a id="schema_TaskReaction"></a>
<a id="tocStaskreaction"></a>
<a id="tocstaskreaction"></a>

```json
{
  "task_id": "736fde4d-9029-4915-8189-01353d6982cb",
  "emoji": "string",
  "count": 0,
  "requester_reacted": false
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|task_id|string(uuid)|true|none|none|
|emoji|string|true|none|none|
|count|integer|true|none|none|
|requester_reacted|boolean|true|none|none|

<h2 id="tocS_TaskReactionToggleRequest">TaskReactionToggleRequest</h2>
<!-- backwards compatibility -->
<a id="schemataskreactiontogglerequest"></a>
<a id="schema_TaskReactionToggleRequest"></a>
<a id="tocStaskreactiontogglerequest"></a>
<a id="tocstaskreactiontogglerequest"></a>

```json
{
  "emoji": "string"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|emoji|string|true|none|none|

<h2 id="tocS_TaskReactionMutationData">TaskReactionMutationData</h2>
<!-- backwards compatibility -->
<a id="schemataskreactionmutationdata"></a>
<a id="schema_TaskReactionMutationData"></a>
<a id="tocStaskreactionmutationdata"></a>
<a id="tocstaskreactionmutationdata"></a>

```json
{
  "task_id": "736fde4d-9029-4915-8189-01353d6982cb",
  "emoji": "string",
  "requester_reacted": true
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|task_id|string(uuid)|true|none|none|
|emoji|string|true|none|none|
|requester_reacted|boolean|true|none|none|

<h2 id="tocS_TaskReactionListResponse">TaskReactionListResponse</h2>
<!-- backwards compatibility -->
<a id="schemataskreactionlistresponse"></a>
<a id="schema_TaskReactionListResponse"></a>
<a id="tocStaskreactionlistresponse"></a>
<a id="tocstaskreactionlistresponse"></a>

```json
{
  "data": [
    {
      "task_id": "123e4567-e89b-12d3-a456-426614174002",
      "emoji": ":thumbsup:",
      "count": 3,
      "requester_reacted": true
    },
    {
      "task_id": "123e4567-e89b-12d3-a456-426614174002",
      "emoji": ":rocket:",
      "count": 1,
      "requester_reacted": false
    }
  ]
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|data|[[TaskReaction](#schemataskreaction)]|true|none|none|

<h2 id="tocS_TaskReactionMutationResponse">TaskReactionMutationResponse</h2>
<!-- backwards compatibility -->
<a id="schemataskreactionmutationresponse"></a>
<a id="schema_TaskReactionMutationResponse"></a>
<a id="tocStaskreactionmutationresponse"></a>
<a id="tocstaskreactionmutationresponse"></a>

```json
{
  "data": {
    "task_id": "123e4567-e89b-12d3-a456-426614174002",
    "emoji": ":thumbsup:",
    "requester_reacted": true
  }
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|data|[TaskReactionMutationData](#schemataskreactionmutationdata)|true|none|none|

