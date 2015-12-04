## Add syntaxic sugar on top of vertx-lang-groovy

If you want to use Vert.x in Groovy with a less idiomatic but more groovy-ish API !

## List of transformations

### WriteStream

Examples : `ServerWebSocket`, `SockJSSocket`, `HttpServerResponse`

| Groovy sugar  | Vert.x standard |
| ------------- | --------------- |
| `stream << data` | `stream.write(data)` |

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
| `ctx++` | `ctx.next()` |


### String

| Groovy sugar  | Vert.x standard |
| ------------- | --------------- |
| `'something' as Buffer` | `Buffer.buffer('something')` |


