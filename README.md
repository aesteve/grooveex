## Add syntaxic sugar on top of vertx-lang-groovy

If you want to use Vert.x in Groovy with a less idiomatic but more groovy-ish API !

Have a look at [the tests](/src/test/groovy/com/github/aesteve/vertx/groovy/specs) if you want to have a glimpse at how the sugar looks in action.

Enjoy as part of a healthy diet :)

## Adding extensions

Since the projects relies on an Groovy extension module, you just have to add the jar to your project. Nothing more.

Example, if you're using Gradle: 
```groovy
repositories {
	jcenter()
}

dependencies {
	compile 'com.github.aesteve:grooveex:0.5'
}
```

And you're done. The new methods will also work with `@TypeChecked` since it's an extension module, not a metaclass.

## Examples

### Basic router, dealing with request / responses

```groovy
Vertx vertx = Vertx.vertx

// Server-side
Router router = vertx.router
router['/hello'] >> { // router.get('hello').handler {
  def name = request.params['name'] // params is available through getParams => as a attribute
  response << "Hello $name"  // it.response().end("Hello $name"), routingContext is implicit, thus response is it.response()
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
Router router = vertx.router
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

Keep in mind that the DSL doesn't force you to write all your handlers within the routingFile.

Vert.x defines a Groovy Handler (middleware) as either a class implementing the `Handler` interface, a closure or a method reference.

That means you can do the following :

```groovy
RouterBuidler.make {
	get('/accessAnObject').handler(new MyHandler())
	get('/accessAMethod').handler(new HandlerWithMultipleMethods().&someMethod)
	get('/accessAClosure', SomeClass.aStaticClosure)
	get('/inline') {
		response.end << "or just inline stuff if it's simple enough" 
	}
}
```

And since you receive the `Router` instance once its built, you can use it programmatically as you're used to with Vert.x ! 

### Split routing rules across multiple files

As soon as your application grows, you'll feel the need to separate concerns and the routing concerns separate.
For instance the part dealing with static resources, server-side templates, could be kept together, whereas middlewares for REST-APIs should be kept in a separate file. 

```groovy
RouterBuilder builder = new RouterBuilder(vertx: vertx)
File routingFolder = new File('/some/path/routing_foler')
Router router = builder(routingFolder.listFiles().collect { it.name.endsWith('.groovy') })
```

### Nesting routes

```groovy
RouterBuilder builder = new RouterBuilder() 
Router router = builder {
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

### Payload & Marshalling

You can set a marshaller to read request body or write response body automatically simply by implementing the `com.github.aesteve.vertx.groovy.io.Marshaller` interface.
By default, since Vert.x already uses Jackson, you can use the `JacksonMarshaller` we provided.

Then, just declare that your router (or route), uses this marshaller:

```groovy
router {
    marshaller 'application/json', new JacksonMarshaller()
    // ...
}
```

#### Read request body using marshaller

```groovy
router {
    marshaller 'application/json', new JacksonMarshaller()
    post('/create') {
        def marshalledBody = body as SomeObject // using 'as' on body will automatically invoke the marshaller
        // ...
    }
}
```

#### Write response body

```groovy
router {
    marshaller 'application/json', new JacksonMarshaller()
    post('/create') {
        def marshalledBody = body as SomeObject
        def something = marshalledBody.transform() // do some stuff
        yield something // when you yield an object, it's set as response payload and calls context.next(), it will be marshalled automatically
    }
}
```

#### Register your own DSL item

Obviously we can't provide you a full list of useful middlewares, handlers. Moreover, you'll probably need a lot of stuff specific to your application. 

Since it's pretty cool to read a routing file in a declarative way (like `expect {something}` for instance), you can register your own handlers/middlewares against RouterBuilder:
```groovy
RouterBuilder builder = new RouterBuilder()
builder.extensions['injectHeader'] = { String headerName, String headerValue ->
	return { RoutingContext ctx ->
		response.headers[headerName] = headerValue
		ctx++
	}
}
builder {
	route('/dateHeader') {
		injectHeader 'Date', "${-> new Date() }" // lazy eval
		get {
			response << 'You should have the date header yay !"
		}
	}
}
```

Or if you prefer putting it into the routing file directly:
```groovy
router {
	extension('injectHeader') { String headerName, String headerValue ->
		return { RoutingContext ctx ->
			response.headers[headerName] = headerValue
			ctx++
		}
	}
	route('/dateHeader') {
		injectHeader 'Date', "${-> new Date() }" // lazy eval
		get {
			response << 'You should have the date header yay !"
		}
	}
}
```

#### Matching multiple http methods

You can use either `|`, `&` or `/` operator to match multiple http methods. Just use the one that suits you the most.

```groovy
router {
  route('/multi') {
    get | post {
      response << 'get or post'
    }
    options & delete {
      response << 'options or delete'
    }
    trace / connect {
      response << 'trace or connect'
    }
  }
}
```

#### Matchin every (declared) http methods

In case you want to match all the http methods you've (or will) declare in your route, you can use the keyword `all`.

```groovy
router {
  route('/all') {
    all {
      response.chunked = true
      it++
    }
    get {
      response + 'get '
      it++
    }
    post {
      response + 'post '
      it++
    }
    all {
      response << 'Done'
    }
  }
}
```

## Complete list of syntaxic sugar

### WriteStream

Examples : `ServerWebSocket`, `SockJSSocket`, `HttpClientRequest`, `HttpServerResponse`

| Groovy sugar  | Vert.x standard |
| ------------- | --------------- |
| `stream += data` | `stream.write(data)` |
| `stream << data` | `stream.end(data)` |


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
| `ctx.redirect 'somewhere', statusCode` | `ctx.response().putHeader(HttpHeaders.LOCATION, 'somewhere').setStatusCode(statusCode).end()` |
| `ctx.user` | `ctx.user()` |
| `ctx.vertx` | `ctx.vertx()` |
| `ctx - 400` | `ctx.fail(400)` |
| `ctx - new RuntimeException()` | `ctx.fail(new RuntimeException())` |
| `ctx++` | `ctx.next()` |
| `ctx >> closure` | `{ res -> if (res.failed) { ctx.fail(res.cause()) } else { closure(res.result()) } }` |

#### Notes

NB : you already can call `ctx++` (without this lib) since the method on `RoutingContext` is already called `next()`

NB : the last method is a very common pattern when you invoke an async method that takes an Handler<AsyncResult> as parameter. If it fails, you just want the context to fail, else, you'll need the result to do something with.

#### Additional methods

You'll find an `ensure` method within `RoutingContext` which is very useful for dealing with a common pattern in Vert.x.

Often, one of the first handlers in your routes will invoke an async service and :

* fail if the service invocation failed ( 500 )
* check the result asynchronously, then :
    * fail with some statusCode if the result doesn't suit you (say, the token is invalid)
    * store the result somewhere, or do something with the result if the results suits you, then call `context.next()`

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

### HttpServerRequest

| Groovy sugar  | Vert.x standard |
| ------------- | --------------- |
| `req.params` | `req.params()` |
| `req.headers` | `req.headers()` |
| `req.method` | `req.method()` |
| `req.path` | `req.path()` |
| `req - '/path'` | `req.path() - '/path'` |
| `req >>> handler` | `req.bodyHandler(handler)` |

### HttpServerResponse

| Groovy sugar  | Vert.x standard |
| ------------- | --------------- |
| `resp.headers` | `resp.headers()` |
| `resp.headers = headers` | `resp.headers().clear().addAll(headers)` |
| `resp++` | `resp.end()` |
| `resp << new JsonBuilder(...)` | `resp.end(new JsonBuilder(...).toString())` |

### HttpClient

| Groovy sugar  | Vert.x standard |
| ------------- | --------------- |
| `client['/path']` | `client.get('/path')` |


### HttpClientRequest

| Groovy sugar  | Vert.x standard |
| ------------- | --------------- |
| `req++` | `req.end()` |
| `req << new JsonBuilder(...)` | `req.end(new JsonBuilder(...).toString())` |
| `req.method` | `req.method()` |
| `req.headers = headers` | `req.headers().clear().addAll(headers)` |

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
| `vertx.router` | `Router.router(vertx)` |

### String

| Groovy sugar  | Vert.x standard |
| ------------- | --------------- |
| `'something' as Buffer` | `Buffer.buffer('something')` |

### JsonBuilder

| Groovy sugar  | Vert.x standard |
| ------------- | --------------- |
| `new JsonBuilder(...) as Buffer` | `Buffer.buffer(new JsonBuilder(...).toString())` |

### Context

| Groovy sugar  | Vert.x standard |
| ------------- | --------------- |
| `context.config` | `context.config()` |

### MultiMap

| Groovy sugar  | Vert.x standard |
| ------------- | --------------- |
| `map[key]` | `map.get(key)` |
| `map[key] = value` | `map.put(key, value)` |
| `map -= key` | `map.remove(key)` |
