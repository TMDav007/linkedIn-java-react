import React, { useState } from 'react'
import classes from "./VerifyEmail.module.scss"
import Box from '../../components/Box/Box'
import Input from '../../components/Input/Input';
import Button from '../../components/Button/Button';
import Layout from '../../components/Layout/Layout';
import { useNavigate } from 'react-router-dom';

function VerifyEmail() {

    const [errorMessage, setErrorMessage] = useState("");
    const [message, setMessage] = useState("");
    const [isLoading, setIsLoading] = useState(false);

    const navigate = useNavigate()

    const validateEmail = async(code: string) => {
        setMessage("");

        try{
            const response = await fetch(
                `${import.meta.env.VITE_API_URL}/api/v1/authentication/validate-email-verification-token?token=${code}`,
                { 
                    method: "PUT",
                    headers: {
                        Authorization: `Bearer ${localStorage.getItem("token")}`,
                    }
                }
            );
            if (response.ok) {
                setErrorMessage("");
                navigate("/");
            }
            const {message} = await response.json();
            setErrorMessage(message)
        }catch (e) {
            console.log(e);
            setErrorMessage("Something went wrong. Please try again")
        }finally {
            setIsLoading(false);
        }
    }

    const sendEmailVerificationToken = async() => {
        setMessage("");

        try{
            const response = await fetch(
                `${import.meta.env.VITE_API_URL}/api/v1/authentication/send-email-verification-token`,
                { 
                    headers: {
                        Authorization: `Bearer ${localStorage.getItem("token")}`,
                    }
                }
            );
            if (response.ok) {
                setErrorMessage("");
                setMessage("Code sent successfully. Please check your email");
                return;
            }
            const {message} = await response.json();
            setErrorMessage(message)
        }catch (e) {
            console.log(e);
            setErrorMessage("Something went wrong. Please try again")
        }finally {
            setIsLoading(false);
        }
    }

  return (
    <Layout className={classes.root}>
         <Box>
            <h1>Verify your Email</h1>

            <form
             onSubmit={async (e) => {
                e.preventDefault();
                setIsLoading(true);
                const code = e.currentTarget.code.value;
                await validateEmail(code);
                setIsLoading(false);
          }}
        >
          <p>Only one step left to complete your registration. Verify your email address.</p>
          <Input type="text" label="Verification code" key="code" name="code" />
          {message ? <p style={{ color: "green" }}>{message}</p> : null}
          {errorMessage ? <p style={{ color: "red" }}>{errorMessage}</p> : null}
          <Button type="submit" disabled={isLoading}>
            {isLoading ? "..." : "Validate email"}
          </Button>
          <Button
            outline
            type="button"
            onClick={() => {
             sendEmailVerificationToken();
            }}
             disabled={isLoading}
          >
            {isLoading ? "..." : "Send again"}
          </Button >
        </form>
         </Box>
    </Layout>
  )
}

export default VerifyEmail