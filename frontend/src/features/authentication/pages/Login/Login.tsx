import React, { FormEvent, useState } from 'react'
import classes from "./Login.module.scss"
import Layout from '../../components/Layout/Layout'
import Box from '../../components/Box/Box'
import Input from '../../components/Input/Input'
import Button from '../../components/Button/Button'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import Separator from '../../components/Separator/Separator'
import { useAuthentication } from '../../contexts/AuthenticationContextProvider'

function Login() {
    const [errorMessage, setErrorMessage] = useState("");
    const [isLoading, setIsLoading] = useState(false);
    const {user, login } = useAuthentication();
    const navigate = useNavigate();
    const location = useLocation();

    const doLogin = async(e: FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        setIsLoading(true);
        const email = e.currentTarget.email.value;
        const password = e.currentTarget.password.value;

        try{
            await login(email, password);
            const destination = location.state?.from || "/";
            navigate(destination)
        }catch (e) {
            if( e instanceof Error) {
                setErrorMessage(e.message)
            } else {
                setErrorMessage("An unknown error occurred");
            }
        } finally {
            setIsLoading(false);
        }
    }

  return (
    <Layout> 
        <div className={classes.root}>
        <Box>
            <h1>Sign in</h1>
            <p>Stay updated on your professional world.</p>
            <form onSubmit={doLogin}>
            <Input label="Email" type="email" id="email" onFocus={() => setErrorMessage("")} />
            <Input
                label="Password"
                type="password"
                id="password"
                onFocus={() => setErrorMessage("")}
            />
            {errorMessage && <p className={classes.error}>{errorMessage}</p>}

            <Button type="submit" disabled={isLoading}>
                {isLoading ? "..." : "Sign in"}
            </Button>
            <Link to="/authentication/request-password-reset">Forgot password?</Link>
            </form>
            <Separator>Or</Separator>
            <div className={classes.register}>
            New to LinkedIn? <Link to="/signup">Join now</Link>
            </div>
      </Box>
        </div>
    </Layout>
   
  )
}

export default Login