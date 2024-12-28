import { createContext, useContext, useEffect, useState } from "react"
import { Navigate, Outlet, useLocation } from "react-router-dom";
import Loader from "../../../components/Loader/Loader";

interface User{
    id: string;
    email: string;
    emailVerified: boolean;
}

interface AuthContextType{
    user: User | null;
    login: (email: string, password: string) => Promise<void>;
    signup: (email: string, password: string) => Promise<void>;
    logout: () => void;
}

const AuthenticationContext = createContext<AuthContextType | null>(null);

export function useAuthentication() {
    return useContext(AuthenticationContext);
}

export default function AuthenticationContextProvider() {

    const [user, setUser] = useState<User | null>(null)
    const [isLoading, setIsLoading] = useState<boolean>(true);
    const location = useLocation()

    const isOnAuthPage = 
    location.pathname === "/login" || 
    location.pathname === "/signup" || 
    location.pathname === "/request-password-reset";

    const login = async(email: string, password: string) => {
        const response = await fetch(import.meta.env.VITE_API_URL + "/api/v1/authentication/login", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({email, password})
        });

        if (response.ok) {
            const {token} = await response.json();
            localStorage.setItem("token", token);
        } else {
            const {message} = await response.json();
            throw new Error(message);
        }
    }

    const signup = async(email: string, password: string) => {
        const response = await fetch(import.meta.env.VITE_API_URL + "/api/v1/authentication/register", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({email, password})
        });

        if (response.ok) {
            const {token} = await response.json();
            localStorage.setItem("token", token);
        } else {
            const {message} = await response.json();
            throw new Error(message);
        }
    }

    const logout = () => {
        localStorage.removeItem("token");
        setUser(null);
    }

    const fetchUser = async() => {

        try{
            const response = await fetch(import.meta.env.VITE_API_URL + "/api/v1/authentication/user", {
                headers: {
                    Authorization: `Bearer ${localStorage.getItem("token")}`,
                }
            });
    
            if (!response.ok) {
                throw new Error("Authentication failed");
            } 
            const user = await response.json();
            setUser(user);
            
        } catch(e) {
            console.log(e);
        } finally {
            setIsLoading(false);
        }
    }

    useEffect(() => {
        if(user) return;
        fetchUser();
    }, [user, location.pathname])

    if(isLoading) {
        return <Loader />
    }

    if (!isLoading && !user && !isOnAuthPage) {
        return <Navigate to="/login" />;
    }

    if (user && user.emailVerified && isOnAuthPage) {
        return <Navigate to="/" />
    }



  return (
    <AuthenticationContext.Provider value={{user, login, signup, logout}}>
        {
            user && !user.emailVerified ? <Navigate to="/verify-email" /> : null
        }
        <Outlet />
        
    </AuthenticationContext.Provider>
  )
}
