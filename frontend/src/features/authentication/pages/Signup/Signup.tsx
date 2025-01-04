import React, { FormEvent, useState } from "react";
import classes from "./Signup.module.scss";
import Box from "../../components/Box/Box";
import Input from "../../../../components/Input/Input";
import Button from "../../../../components/Button/Button";
import Separator from "../../components/Separator/Separator";
import { Link, useLocation, useNavigate } from "react-router-dom";
import { useAuthentication } from "../../contexts/AuthenticationContextProvider";

function Signup() {
  const [errorMessage, setErrorMessage] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const { signup } = useAuthentication();

  const navigate = useNavigate();
  const location = useLocation();

  const doSignup = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setIsLoading(true);
    const email = e.currentTarget.email.value;
    const password = e.currentTarget.password.value;

    try {
      await signup(email, password);
      navigate("/");
    } catch (e) {
      if (e instanceof Error) {
        setErrorMessage(e.message);
      } else {
        setErrorMessage("An unknown error occurred");
      }
    } finally {
      setIsLoading(false);
    }
  };
  return (
    <div className={classes.root}>
      <Box>
        <h1>Sign up</h1>
        <p>Make the most of your professional life.</p>
        <form onSubmit={doSignup}>
          <Input type="email" id="email" label="Email" />
          <Input type="password" id="password" label="Password" />
          {errorMessage ? <p className={classes.error}>{errorMessage}</p> : ""}
          <p className={classes.disclaimer}>
            By clicking Agree & Join or Continue, you agree to LinkedIn's{" "}
            <a href="">User Agreement</a>, <a href="">Privacy Policy</a>, and{" "}
            <a href="">Cookie Policy</a>.
          </p>
          <Button type="submit" disabled={isLoading}>
            {" "}
            Agree & Join
          </Button>
        </form>
        <Separator>or</Separator>
        <div className={classes.register}>
          Already on LinkedIn? <Link to={"/authentication/login"}>Sign in</Link>
        </div>
      </Box>
    </div>
  );
}

export default Signup;
