import com.github.aesteve.vertx.groovy.io.impl.JacksonMarshaller
import controllers.TestController
import controllers.TestStaticController
import groovy.json.JsonBuilder
import io.vertx.groovy.core.buffer.Buffer
import io.vertx.groovy.ext.web.templ.HandlebarsTemplateEngine
import model.Dog

import static io.vertx.core.http.HttpHeaders.AUTHORIZATION
import static io.vertx.core.http.HttpHeaders.DATE


TestController ctrlerInstance = new TestController()

router {
	extension('setDateHeader') { format ->
		return { ctx ->
			response.headers['X-MyCustomDate'] = new Date().format(format)
			ctx++
		}
	}
	get('/simpleGet') {
		response << 'Simple GET'
	}
	route('/handlers') {
		produces 'application/json'
		consumes 'application/json'
		get {
			response << new JsonBuilder([result: 'GET']) as Buffer
		}
		post {
			response << bodyAsString // simply echoes body
		}
	}
	get('/staticClosure') >> TestStaticController.testClosure
	get('/controllerInstance') >> ctrlerInstance.&someMethod
	staticHandler '/assets/*'
	staticHandler('/instrumented-assets/*') {
		get {
			response.headers['X-Custom-Header'] = 'instrumented'
			it++
		}
	}
	templateHandler("/handlebars/*", HandlebarsTemplateEngine.create()) {
		expect { params['name'] }
		get { ctx ->
			ctx['name'] = params['name']
			ctx++
		}
	}
	subRouter('/sub') {
		cookies = true
		staticHandler '/assets/*', 'webroot/subdirectory'
		get('/firstSubRoute') >> {
			response << 'firstSubRoute'
		}
		route '/secondSubRoute', {
			get {
				response << 'secondSubRoute'
			}
		}
	}
	sockJS '/sockjs/*', { socket ->
		socket >> socket.&write
	}
	//favicon "my_favicon.ico"
	route('/login') {
		session([clustered: true])
		get {
			response << session["test"]
		}
	}
	route('/cors/test') {
		cors "*"
		get {
			response << "CORS"
		}
	}
	route('/blocking') {
		blocking = true
		get {
			sleep 3000 // check no exception is thrown in console
			response << 'done !'
		}
	}
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
			route('/magic') { // /sugar/sex/magic
				blocking = true
				cors '*'
				get {
					response.headers['X-Song'] = 'Under the bridge'
					response.headers['X-Artist'] = 'RHCP'
					response.headers[DATE] = 'Tue, 10 Mar 1992 12:45:26 GMT'
					response.chunked = true
					response += 'Is the city I live in...'
					sleep 1000
					response += '...The city of Angels'
					sleep 1000
					response << ' - RHCP (1991)'
				}
			}
		}
	}
	route('/expect') {
		expect { params['exists'] }
		expect { Long.valueOf params['long'] }
		expect { headers[AUTHORIZATION]?.indexOf('token ') == 0 }
		get {
			response << "everything's fine"
		}
	}
	route('/check') {
		check { params['token'] } | 401
		check { params['token'] == 'magic' } | 403
		get {
			response << "everything's fine"
		}
	}
	subRouter('/json') {
		consumes 'application/json'
		produces 'application/json'
		get('/pure') {
			response << 'json'
		}
		route('/plain') {
			consumes 'text/plain'
			produces 'text/plain'
			get {
				response << 'json|plain'
			}
		}
	}
	route('/regex/*') {
		it ~/\/regex\/([^\/]+)/ // route.pathRegex(/\/regex\/([^\/]+)/)
		get {
			response << params['param0']
		}
	}
	subRouter('/marshall') {
		marshaller 'application/json', new JacksonMarshaller()
		post('/dog') { // echo back
			yield body as Dog
		}
	}
	route('/extensions') {
		route('/date') {
			setDateHeader 'DD/MM/yyyy HH:mm:ss'
			get {
				response << 'check the "X-MyCustomDate" header :)'
			}
		}
	}
	route('/withsubroute') {
		consumes 'application/json'
		produces 'application/json'
		get {
			response << 'get'
		}
		get('/:id') {
			response << "get ${params['id']}"
		}
	}
}