# Popular Movies

Project 1 for the Associate Android Developer Fast Track.

##Project Overview

Most of us can relate to kicking back on the couch and enjoying a movie with friends and family. In this project, you’ll build an app to allow users to discover the most popular movies playing. We will split the development of this app in two stages. First, let's talk about stage 1. In this stage you’ll build the core experience of your movies app.

##First Stage
You app will:

* Present the user with a grid arrangement of movie posters upon launch.
* Allow your user to change sort order via a setting:
 * The sort order can be by most popular or by highest-rated
* Allow the user to tap on a movie poster and transition to a details screen with additional information such as:
 * original title
 * movie poster image thumbnail
 * A plot synopsis (called overview in the api)
 * user rating (called vote_average in the api)
 * release date

##Second stage

In this stage you’ll add additional functionality to the app you built in Stage 1.
* You’ll add more information to your movie details view:
* You’ll allow users to view and play trailers ( either in the youtube app or a web browser).
* You’ll allow users to read reviews of a selected movie.
* You’ll also allow users to mark a movie as a favorite in the details view by tapping a button(star). This is for a local movies collection that you will maintain and does not require an API request*.
* You’ll modify the existing sorting criteria for the main view to include an additional pivot to show their favorites collection.

Lastly, you’ll optimize your app experience for tablet.

##API Key

To fetch popular movies, you will use the API from themoviedb.org:

http://api.themoviedb.org/3/movie/popular?api_key=[YOUR_API_KEY]

Please, add your API Key in the QueryUtils.java file replacing [YOUR_API_KEY].
