/* globals require, console */
var restify = require("restify"),
    userSave = require("save")("user"),
    server = restify.createServer({
        name: "running-dj"
    });


// Start the server listening on port 3000
server.listen(3000, function () {
    console.log(server.name + " listening on " + server.url);
});

server
    .use(restify.fullResponse())
    .use(restify.bodyParser());

// Get a single user by their user id
server.get("/user/:id", function (req, res, next) {
    userSave.findOne({ _id: req.params.id }, function (error, user) {
        if (error) {
            return next(new restify.InvalidArgumentError(
                JSON.stringify(error.errors)
            ));
        }
        
        if (user) {
            res.send(user);
        } else {
            res.send(404);
        }
    });
});

// Create a new user
server.post("/user", function (req, res, next) {

    if (typeof req.params.name === "undefined") {
        return next(new restify.InvalidArgumentError("Name must be supplied"));
    }

    // Create the user using the persistence engine
    userSave.create({ name: req.params.name }, function (error, user) {
        if (error) {
            return next(new restify.InvalidArgumentError(
                JSON.stringify(error.errors)
            ));
        }

        res.send(201, user);
    });
});

// Update a user by their id
server.put("/user/:id", function (req, res, next) {

    if (typeof req.params.name === "undefined") {
        return next(new restify.InvalidArgumentError("Name must be supplied"));
    }
    
    userSave.update({ _id: req.params.id, name: req.params.name }, function (error, user) {

        if (error) {
            return next(new restify.InvalidArgumentError(
                JSON.stringify(error.errors)));
        }

        res.send(200);
    });
});

// Delete user with the given id
server.del("/user/:id", function (req, res, next) {

    userSave.delete(req.params.id, function (error, user) {

        // If there are any errors, pass them to next in the correct format
        if (error) {
            return next(new restify.InvalidArgumentError(
                JSON.stringify(error.errors)
            ));
        }

        res.send(200);
    });
});