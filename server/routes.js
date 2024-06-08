const registerHandler = require("./handlers/registerHandler");
const loginHandler = require("./handlers/loginHandler");
const getUserHandler = require("./handlers/getUserHandler");
const postHandler = require("./handlers/postHandler");
const deletePostHandler = require("./handlers/deletePostHandler");
const userPostsHandler = require("./handlers/userPostsHandler");
const allPostsHandler = require("./handlers/allPostsHandler");
const singlePostHandler = require("./handlers/singlePostHandler");
const searchHandler = require("./handlers/searchHandler");
const searchHistoryHandler = require("./handlers/searchHistoryHandler");
const deleteSearchHistoryHandler = require("./handlers/deleteSearchHistoryHandler");
const editPostHandler = require("./handlers/editPostHandler");

const routes = [
  {
    method: "POST",
    path: "/register",
    handler: registerHandler,
  },
  {
    method: "POST",
    path: "/login",
    handler: loginHandler,
  },

  {
    method: "GET",
    path: "/user/{userId}",
    handler: getUserHandler,
  },

  {
    method: "POST",
    path: "/{userId}/post",
    options: {
      payload: {
        output: "stream",
        parse: true,
        allow: "multipart/form-data",
        multipart: true,
      },
    },
    handler: postHandler,
  },
  {
    method: "PUT",
    path: "/{userId}/post/{postId}",
    options: {
      payload: {
        output: "stream",
        parse: true,
        allow: "multipart/form-data",
        multipart: true,
      },
    },
    handler: editPostHandler,
  },
  {
    method: "DELETE",
    path: "/{userId}/post/{postId}",
    handler: deletePostHandler,
  },
  {
    method: "GET",
    path: "/user/{userId}/posts",
    handler: userPostsHandler,
  },
  {
    method: "GET",
    path: "/posts",
    handler: allPostsHandler,
  },
  {
    method: "GET",
    path: "/post/{postId}",
    handler: singlePostHandler,
  },
  {
    method: "GET",
    path: "/{userId}/search",
    handler: searchHandler,
  },
  {
    method: "GET",
    path: "/{userId}/search_history",
    handler: searchHistoryHandler,
  },
  {
    method: "DELETE",
    path: "/{userId}/search_history/{entryId}",
    handler: deleteSearchHistoryHandler,
  },
];

module.exports = routes;
