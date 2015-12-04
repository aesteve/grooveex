## Add syntaxic sugar on top of vertx-lang-groovy

If you want to use Vert.x in Groovy with a less idiomatic but more groovy-ish API !

Have a look at [the tests](/src/test/groovy/com/github/aesteve/vertx/groovy/specs) if you want to have a glimpse at how the sugar looks in action.

## List of transformations

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

### RoutingContext

| Groovy sugar  | Vert.x standard |
| ------------- | --------------- |
| `ctx['key']` | `ctx.get('key')` |
| `ctx['key'] = value` | `ctx.put('key', value)` |
| `ctx.response` | `ctx.response()` |
| `ctx.request` | `ctx.request()` |
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


### String

| Groovy sugar  | Vert.x standard |
| ------------- | --------------- |
| `'something' as Buffer` | `Buffer.buffer('something')` |


