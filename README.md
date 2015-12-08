## Add syntaxic sugar on top of vertx-lang-groovy

If you want to use Vert.x in Groovy with a less idiomatic but more groovy-ish API !

Have a look at [the tests](/src/test/groovy/com/github/aesteve/vertx/groovy/specs) if you want to have a glimpse at how the sugar looks in action.

Enjoy as part of a healthy diet :)

## Adding extensions

Since the projects relies on an Groovy extension module, you just have to add the jar to your project. Nothing more.

Example, if you're using Gradle: 
```groovy
repositories {
	maven {
		url  "http://dl.bintray.com/aesteve/maven" // waiting for it to be added to jCenter soon
	}
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
  def name = it.request.params['name'] // params is available through getParams => as a attribute
  it.response << "Hello $name"  // it.response.end("Hello $name")
}
server.requestHandler router.&accept
vertx.createHttpServer().listen()

// Client side
HttpClient client = vertx.createHttpClient()
HttpRequest req = client["/hello?name=World"] // client.get "/hello?name=World"
req >> { response -> // req.handler {
  response >>> { // response.bodyHandler
    assert it.toString('UTF-8') == 'Hello World'
  } 
} 
req++ // req.end()
```

### Pumping streams

```groovy
Vertx vertx = Vertx.vertx

// Server-side
Router router = Router.router vertx
router['/pump'] >> {
  Buffer received = Buffer.buffer()
  it.request >> { buff -> // request handler
    received += buff // appends the received buffer (received << buff also works) 
  }
}
server.requestHandler router.&accept
vertx.createHttpServer().listen()

// Client side (sends a file to the server)
HttpClient client = vertx.createHttpClient()
HttpRequest req = client["/pump"]
vertx.fileSystem.open 'test-file', [:], { res ->
  AsyncFile file = res.result()
  Pump filePump = file | req // createPump between readstream and writestream
  filePump.start()
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

## Complete sugar list

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
| `ctx++` | `ctx.next()` |

NB : you already can call `ctx++` (without this lib) since the method on `RoutingContext` is already called `next()`

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
| `resp << 'done'` | `resp.end('done')` |

### HttpClient

| Groovy sugar  | Vert.x standard |
| ------------- | --------------- |
| `client['/path']` | `client.get('/path')` |


### HttpClientRequest

| Groovy sugar  | Vert.x standard |
| ------------- | --------------- |
| `req++` | `req.end()` |

### HttpClientResponse

| Groovy sugar  | Vert.x standard |
| ------------- | --------------- |
| `resp >>> handler` | `resp.bodyHandler(handler)` |

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


### RouterBuilder

On top of the syntaxic sugar, you can use the `RouterBuilder` class to create a vertx-web router from a closure.

Just have a look at the [routing file example](blob/master/src/test/resources/routes.groovy) to see how it looks like.
It makes use of the overloaded operators like `<<` or `>>` but also "wraps" Vert.x's handler closure to inject `RoutingContext` as delegate, so that you can directly write `response.headers` for instance and not `it.response().headers`.
Every method available in `RoutingContext` will be directly available within your closure.