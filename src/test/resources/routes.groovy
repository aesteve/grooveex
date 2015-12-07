import controllers.TestController
import controllers.TestStaticController
import groovy.json.JsonBuilder

TestController ctrlerInstance = new TestController()

router {
    get "/simpleGet", { context ->
        response << "Simple GET"
    }
    route "/handlers", {
        produces "application/json"
        consumes "application/json"
        get {
            response << new JsonBuilder([result: "GET"])
        }
        post {
            response << bodyAsString
        }
    }
    get "/staticClosure", TestStaticController.testClosure
    get "/controllerInstance", ctrlerInstance.&someMethod
    staticHandler "/assets/*"
    staticHandler "/instrumented-assets/*", {
        get {
            request.headers["X-Custom-Header"] = "instrumented"
            it++
        }
    }
    // templateHandler "/dynamic/*", HandlebarsTemplateEngine.create()
    subRouter "/sub", {
        cookies: true
        staticHandler "/assets/*", "webroot/subDirectory"
        get "/firstSubRoute", {
            response << "firstSubRoute"
        }
    }
    sockJS "/sockjs/*", { socket ->
        socket.handler socket.&write
    }
    //favicon "my_favicon.ico"
    route "/login", {
        session([clustered: true])
        get {
            response << session["test"]
        }
    }
    route "/cors/test", {
        cors "*"
        get {
            response << "CORS"
        }
    }
}