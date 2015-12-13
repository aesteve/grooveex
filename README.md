## Add syntaxic sugar on top of vertx-lang-groovy

If you want to use Vert.x in Groovy with a less idiomatic but more groovy-ish API !

Have a look at [the tests](/src/test/groovy/com/github/aesteve/vertx/groovy/specs) if you want to have a glimpse at how the sugar looks in action.

Enjoy as part of a healthy diet :)

## Adding extensions

Since the projects relies on an Groovy extension module, you just have to add the jar to your project. Nothing more.

Example, if you're using Gradle: 
```groovy
repositories {
	jCenter()
}

dependencies {
	compile 'com.github.aesteve:vertx-groovy-sugar:0.1'
}
```

And you're done. The new methods will also work with `@TypeChecked` since it's an extension module, not a metaclass.

## Examples

### Basic router, dealing with request / responses

```groovy
Vertx vertx = Vertx.vertx

// Server-side
Router router = Router.router vertx
router['/hello'] >> { // router.route('hello').handler {
  def name = request.params['name'] // params is available through getParams => as a attribute
  response << "Hello $name"  // it.response.end("Hello $name")
}
server.requestHandler router.&accept
vertx.createHttpServer().listen()

// Client side
HttpClient client = vertx.createHttpClient()
HttpRequest req = client["/hello?name=World"] // client.get "/hello?name=World"
req >> { response -> // req.handler {
  response >>> { // response.bodyHandler
    assert it as String == 'Hello World' // as String => buffer.toString 'UTF-8'
  } 
} 
req++ // req.end()
```

### Dealing with async results / futures

```groovy
vertx.executeBlocking({ future ->
  try {
    sleep 1000
    future += 'completed !' // plus operator means complete('...'), plusplus operator means complete() 
  } catch(all) {
    future -= 'interrupted :(' // minus operator means fail
  }
}, { asyncResult ->
  if (asyncResult) { // asBoolean method overloading => you can use it in an if statement to check if succeeded() 
    println 'It has succeeded !'
  }
  boolean successful = +asyncResult && asyncResult.result == 'completed' // +asyncResult is asyncResult.succeeded() and getResult() is defined 
  boolean unsuccessful = -asyncResult || !asyncResult.result == 'completed' // -asyncResult is asyncResult.failed() and getResult() is defined
})

```


### Pumping streams

```groovy
Vertx vertx = Vertx.vertx

// Server-side
Router router = Router.router vertx
router['/pump'] >> {
  Buffer received = Buffer.buffer()
  request >> { buff -> // request handler
    received += buff // appends the received buffer (received << buff also works) 
  }
}
server.requestHandler router.&accept
vertx.createHttpServer().listen()

// Client side (sends a file to the server)
HttpClient client = vertx.createHttpClient()
HttpRequest req = client["/pump"]
vertx.fileSystem.open 'test-file', [:], { res ->
  AsyncFile file = res.result
  Pump filePump = file | req // createPump between readstream and writestream
  filePump++
}
```

### Dealing with the event bus

```groovy
Vertx vertx = Vertx.vertx
EventBus eb = vertx.eventBus
Buffer msg = 'Hello !' as Buffer // Buffer.buffer('Hello !')
eb['some-address'] >> { message -> // eb.consumer('some-address', { ... 
  println "Received : ${message.body}" // message.body()
}
eb['some-address'] << 'Hello !' // eb.send('some-address', 'Hello !')
```

### Invoking an async service
```groovy
router.get '/async' >> { context ->
  invokeAsyncMethod request.params['something'], fail | { result ->
    response << result
  }
}
```
Equivalent to : 
```groovy
router.get('/async').handler { context ->
  invokeAsyncMethod context.request().params['something'], { res ->
    if (res.failed()) {
      context.fail(res.cause())
    } else {
      response << res.result()
    }
  }
}
```


## Rule of thumb

Basically : 

* `a + b` means either `a.append(b)`, `a.write(b)` or `a.complete(b)`
* `a - b` means `a.fail(b)`
* `a++` means `a.complete()` or `a.next()`
* `a << b` means `a.end(b)`
* `a >> b` means `a.handler(b)`
* `a >>> b` means `a.bodyHandler(b)` (a "global" handler)
* `a | b` means `a.pipe(b)` (pumps)
* `+a` means `a.succeeeded()` `a.completed()`
* `-a` means `a.failed()`

Every getter method like `a.response()`, `a.request()` will also be available as an object attribute `a.response`, `a.request` (through `a.getResponse()` - Groovy convention -).  




## RouterBuilder

On top of the syntaxic sugar, you can use the `RouterBuilder` class to create a vertx-web router from a closure.

Just have a look at the [routing file example](blob/master/src/test/resources/routes.groovy) to see how it looks like.
It makes use of the overloaded operators like `<<` or `>>` but also "wraps" Vert.x's handler closure to inject `RoutingContext` as delegate, so that you can directly write `response.headers` for instance and not `it.response().headers`.
Every method available in `RoutingContext` will be directly available within your closure.

### Nesting routes

```groovy
RouterBuilder builder = new RouterBuilder() 
Router router = builder.make {
  route('/blood') {
    // ...
    route('/sugar') {
      get {
        response << 'Yes please !'
      }
      post {
        String sent = body as String
        if (sent == 'I want that sugar sweet') {
          response << "Don't let nobody touch it"
        } else {
          fail 400
        }
      }
      route('/sex') {
        route('/magic') {
          blocking = true
          cors '*'
          get {
            response.headers['X-Song'] = 'Under the bridge'
            response.headers['X-Artist'] = 'RHCP'
            response.headers[HttpHeaders.DATE] = "Tue, 10 Mar 1992 12:45:26 GMT"
            response.chunked = true
            response += 'Is the city I live in...'
            sleep 1000
            response += '...The city of Angels'
            sleep 1000
            response << '- RHCP (1991)'
          }
        }
      }
    }
  }
}
```

### Check/Expect

```groovy
route('/expect') { // fails with 400, even if an exception is thrown (be careful !)
	expect { params['exists'] }
	expect { Long.valueOf params['long'] } // can throw NumberFormatException -> but 400 anyway
	expect { headers[AUTHORIZATION]?.indexOf('token ') == 0 }
	get {
		response << "everything's fine"
	}
}
route('/check') { // fails with the specified statusCode, and doesn't swallow exceptions
	check { params['token'] } | 401
	check { params['token'] == 'magic' } | 403
	get {
		response << "everything's fine"
	}
}
```

## Complete list of syntaxic sugar

### WriteStream

Examples : `ServerWebSocket`, `SockJSSocket`, `HttpServerResponse`

| Groovy sugar  | Vert.x standard |
| ------------- | --------------- |
| `stream += data` | `stream.write(data)` |

In case you're wondering I chose the `<<` operator to mean "end the stream". cf. `HttpServerResponse` for instance.
Don't hesitate to (gently ;) ) let me know what you think about it.

### ReadStream

Examples : `WebSocket`, `HttpServerRequest`, ...

| Groovy sugar  | Vert.x standard |
| ------------- | --------------- |
| `stream >> handler` | `stream.handler(handler)` |
| `stream | other` | `Pump.pump(stream, other)` |

### Route

| Groovy sugar  | Vert.x standard |
| ------------- | --------------- |
| `route >> handler` | `route.handler(handler)` |
| `route ~/somePattern/` | `route.pathRegex("somePattern")` |

### Router

| Groovy sugar  | Vert.x standard |
| ------------- | --------------- |
| `router['route'] = handler` | `router.route('route').handler(handler)` |

### Buffer

| Groovy sugar  | Vert.x standard |
| ------------- | --------------- |
| `buff << other` | `buffer.appendBuffer(other)` |
| `buff << 'something'` | `buffer.appendString('something')` |
| `buff << "hello $name"` | `buffer.appendString("hello $name".toString()")` |
| `buff += other` | `buffer.appendBuffer(other)` |
| `buff += 'something'` | `buffer.appendString('something')` |
| `buff += "hello $name"` | `buffer.appendString("hello $name".toString()")` |
| `buff as String` | `buffer.toString('UTF-8')` |

### RoutingContext

| Groovy sugar  | Vert.x standard |
| ------------- | --------------- |
| `ctx['key']` | `ctx.get('key')` |
| `ctx['key'] = value` | `ctx.put('key', value)` |
| `ctx.cookies` | `ctx.cookies()` |
| `ctx.mountPoint` | `ctx.mountPoint()` |
| `ctx.normalisedPath` | `ctx.normalisedPath()` |
| `ctx.response` | `ctx.response()` |
| `ctx.request` | `ctx.request()` |
| `ctx.session` | `ctx.session()` |
| `ctx.statusCode` | `ctx.statusCode()` |
| `ctx.user` | `ctx.user()` |
| `ctx.vertx` | `ctx.vertx()` |
| `ctx - 400` | `ctx.fail(400)` |
| `ctx - new RuntimeException()` | `ctx.fail(new RuntimeException())` |
| `ctx++` | `ctx.next()` |
| `ctx >> closure` | `{ res -> if (res.failed) { ctx.fail(res.cause()) } else { closure(res.result()) } }` |

#### Additional methods

You'll find an `ensure` method within `RoutingContext` which is very useful for dealing with a common pattern in Vert.x.

Often, one of the first handlers in your routes will invoke an async service and :

* fail if the service invocation failed ( 500 )
* check the result asynchronously, then :
** fail with some statusCode if the result doesn't suit you (say, the token is invalid)
** store the result somewhere, or do something with the result if the results suits you, then call `context.next()`

In action :

```groovy
router.get('/api/1/*').handler { ctx ->
    accessTokenChecker.check(ctx.request().params().get('accessToken'), {
        if (res.failed()) {
            ctx.fail res.cause()
        } else {
            User user = res.result()
            if (user && user.validated) {
                ctx.setUser(user)
                ctx.next()
            } else {
                ctx.fail 401
            }
        }
    })
}
```

Here's the `ensure` equivalent (buckle up !)

```groovy
router.get('/api/1/').handler { ctx ->
    accessTokenChecker.check(ctx.request().params().get('accessToken'), ctx.ensure({ it && it.validated }) & { user = it } | 401)
}
```

Now let's benefit of full sugar and rewrite it :
```groovy
router.get('/api/1/') >> {
    def accessToken = params['accessToken']
    def putTokenIfExistsOrFailWith401 = ensure({ it && it.validated }) & { user = it } | 401
    accessTokenChecker.check accessToken, putTokenIfExistsOrFailWith401 
}
```

This is especially useful for storing and composing small pieces of reusable logic into closures, like so :

```groovy
def userHasValidAccount = { user ->
    user && user.validated
}
router.get('/api/1/') >> {
    def accessToken = params['accessToken']
    def putTokenIfExistsOrFailWith401 = ensure(userHasValidAccount) & { user = it } | 401
    accessTokenChecker.check accessToken, putTokenIfExistsOrFailWith401 
}
```


#### Notes

NB : you already can call `ctx++` (without this lib) since the method on `RoutingContext` is already called `next()`

NB : the last method is a very common pattern when you invoke an async method that takes an Handler<AsyncResult> as parameter. If it fails, you just want the context to fail, else, you'll need the result to do something with.

### HttpServerRequest

| Groovy sugar  | Vert.x standard |
| ------------- | --------------- |
| `req.params` | `req.params()` |
| `req.headers` | `req.headers()` |
| `req >>> handler` | `req.bodyHandler(handler)` |

### HttpServerResponse

| Groovy sugar  | Vert.x standard |
| ------------- | --------------- |
| `resp.headers` | `resp.headers()` |
| `resp++` | `resp.end()` |
| `resp << buffer` | `resp.end(buffer)` |
| `resp << object` | `resp.end(object.toString())` |

### HttpClient

| Groovy sugar  | Vert.x standard |
| ------------- | --------------- |
| `client['/path']` | `client.get('/path')` |


### HttpClientRequest

| Groovy sugar  | Vert.x standard |
| ------------- | --------------- |
| `req++` | `req.end()` |
| `req << buffer` | `req.end(buffer)` |
| `req << object` | `req.end(object.toString())` |

### HttpClientResponse

| Groovy sugar  | Vert.x standard |
| ------------- | --------------- |
| `resp >>> handler` | `resp.bodyHandler(handler)` |
| `resp.headers` | `resp.headers()` |

### Message

| Groovy sugar  | Vert.x standard |
| ------------- | --------------- |
| `msg.address` | `msg.address()` |
| `msg.replyAddress` | `msg.replyAddress()` |
| `msg.headers` | `msg.headers()` |
| `msg.body` | `msg.body()` |
| `msg << 'reply'` | `msg.reply('reply')` |

### AsyncResult

| Groovy sugar  | Vert.x standard |
| ------------- | --------------- |
| `ar.failed` | `ar.failed()` |
| `-ar` | `ar.failed()` |
| `ar.succeeded` | `ar.succeeded()` |
| `+ar` | `ar.succeeded()` |
| `ar.result` | `ar.result()` |
| `ar.cause` | `ar.cause()` |
| `if (ar) {` | `if (ar?.succeeded()) {` |


### Future

| Groovy sugar  | Vert.x standard |
| ------------- | --------------- |
| `f += result` | `f.complete(result)` |
| `f -= cause` | `f.fail(cause)` |
| `f + result` | `f.complete(result)` |
| `f - cause` | `f.fail(cause)` |
| `f++` | `f.complete()` |


### Pump
| Groovy sugar  | Vert.x standard |
| ------------- | --------------- |
| `pump++` | `pump.start()` |
| `pump--` | `pump.stop()` |


### EventBus

| Groovy sugar  | Vert.x standard |
| ------------- | --------------- |
| `eb['address'] >> handler` | `eb.consumer('address', handler)` |
| `eb['address'] << msg` | `eb.send(address, msg)` |
| `eb['address'] ** msg` | `eb.publish(address, msg)` |

### Vertx
| Groovy sugar  | Vert.x standard |
| ------------- | --------------- |
| `Vertx.vertx` | `Vertx.vertx()` |
| `vertx.eventBus` | `vertx.eventBus()` |
| `vertx.fileSystem` | `vertx.fileSystem()` |

### String

| Groovy sugar  | Vert.x standard |
| ------------- | --------------- |
| `'something' as Buffer` | `Buffer.buffer('something')` |
