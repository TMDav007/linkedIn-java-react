import { useLocation, useNavigate, useSearchParams } from "react-router-dom";
import { useAuthentication } from "../contexts/AuthenticationContextProvider";
import { useEffect, useState } from "react";

const GOOGLE_OAUTH2_CLIENT_ID = import.meta.env.VITE_GOOGLE_OAUTH_CLIENT_ID;
const VITE_GOOGLE_OAUTH_URL = import.meta.env.VITE_GOOGLE_OAUTH_URL;
const ANTI_FORGERY_TOKEN = import.meta.env.VITE_ANTI_FORGERY_TOKEN;

export default function useOauth(page: "login" | "signup") {
  const [searchParams, setSearchParams] = useSearchParams();
  const navigate = useNavigate();
  const location = useLocation();
  const { ouathLogin } = useAuthentication();
  const code = searchParams.get("code");
  const state = searchParams.get("state");
  const error = searchParams.get("error");
  const [isOauthInProgress, setIsOauthInProgress] = useState(
    code !== null || error !== null
  );
  const [oauthError, setOauthError] = useState("");

  useEffect(() => {
    async function fetchData() {
      if (error) {
        if (error === "access_denied") {
          setOauthError("You denied access to your Google account.");
        } else {
          setOauthError("An unknown error occurred.");
        }
        setIsOauthInProgress(false);
        setSearchParams({});
        return;
      }

      if (!code || !state) return;

      const { destination, antiForgeryToken } = JSON.parse(state);

      if (antiForgeryToken !== ANTI_FORGERY_TOKEN) {
        setOauthError("Invalid state parameter.");
        setIsOauthInProgress(false);
        setSearchParams({});
        return;
      }

      try {
        await ouathLogin(code, page);

        setTimeout(() => {
          setIsOauthInProgress(false);
          setSearchParams({});
          console.log("destination", destination);
          navigate(destination || "/");
        }, 1000);
      } catch (error) {
        if (error instanceof Error) {
          setOauthError(error.message);
        } else {
          setOauthError("An unknown error occurred.");
        }
        setIsOauthInProgress(false);
        setSearchParams({});
      }
    }

    fetchData();
  }, [code, error, navigate, ouathLogin, page, setSearchParams, state]);

  return {
    isOauthInProgress,
    oauthError,
    startOauth: () => {
      const redirectUri = `${window.location.origin}/authentication/${page}`;
      window.location.href = `${VITE_GOOGLE_OAUTH_URL}?client_id=${GOOGLE_OAUTH2_CLIENT_ID}&redirect_uri=${redirectUri}&scope=openid+email+profile&response_type=code&state=${JSON.stringify(
        {
          antiForgeryToken: ANTI_FORGERY_TOKEN,
          destination: location.state?.from || "/",
        }
      )}`;
    },
  };
}
