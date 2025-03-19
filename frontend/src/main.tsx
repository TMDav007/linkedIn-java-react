import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import "./index.scss";
import {
  createBrowserRouter,
  Navigate,
  RouterProvider,
} from "react-router-dom";

import Login from "./features/authentication/pages/Login/Login";
import Signup from "./features/authentication/pages/Signup/Signup";
import ResetPassword from "./features/authentication/pages/ResetPassword/ResetPassword";
import VerifyEmail from "./features/authentication/pages/VerifyEmail/VerifyEmail";
import { AuthenticationContextProvider } from "./features/authentication/contexts/AuthenticationContextProvider";
import AuthenticationLayout from "./features/authentication/components/AuthenticationLayout/AuthenticationLayout";
import ApplicationLayout from "./components/ApplicationLayout/ApplicationLayout";
import Profile from "./features/authentication/pages/Profile/Profile";
import Feed from "./features/feed/pages/Feed/Feed";
import Notifications from "./features/feed/pages/Notifications/Notifications";
import PostPage from "./features/feed/pages/Post/Post";

const router = createBrowserRouter([
  {
    element: <AuthenticationContextProvider />,
    children: [
      {
        path: "/",
        element: <ApplicationLayout />,
        children: [
          {
            index: true,
            element: <Feed />,
          },
          {
            path: "posts/:id",
            element: <PostPage />,
          },
          {
            path: "network",
            element: <div>psts</div>,
            // children: [
            //   {
            //     index: true,
            //     element: <Navigate to="invitations" />,
            //   },
            //   {
            //     path: "invitations",
            //     element: <Invitations />,
            //   },
            //   {
            //     path: "connections",
            //     element: <Connections />,
            //   },
            // ],
          },
          {
            path: "messaging",
            element: <div>psts</div>,
            // children: [
            //   {
            //     path: "conversations/:id",
            //     element: <Conversation />,
            //   },
            // ],
          },
          {
            path: "notifications",
            element: <Notifications />,
          },
          // {
          //   path: "profile/:id",
          //   element: <Profile />,
          // },
          {
            path: "settings",
            element: <div>psts</div>,
          },
        ],
      },
      {
        path: "/authentication",
        element: <AuthenticationLayout />,
        children: [
          {
            path: "login",
            element: <Login />,
          },
          {
            path: "signup",
            element: <Signup />,
          },
          {
            path: "request-password-reset",
            element: <ResetPassword />,
          },
          {
            path: "verify-email",
            element: <VerifyEmail />,
          },
          {
            path: "profile/:id",
            element: <Profile />,
          },
        ],
      },
      {
        path: "*",
        element: <Navigate to="/" />,
      },
    ],
  },
]);

createRoot(document.getElementById("root")!).render(
  <StrictMode >
    <RouterProvider router={router} />
  </StrictMode>
);
