
import classes from "./AuthenticationLayout.module.scss"
import { Outlet } from 'react-router-dom'

function AuthenticationLayout() {
  return (
    <div className={classes.root}>
        <header className={classes.container}>
            <a href="/"><img src="/logo.svg"  className={classes.logo} alt=""/></a>
        </header>
        <main className={classes.container}>{<Outlet />}</main>
        <footer>
            <ul className={classes.container}>
                <li>
                    <img src="/logo-dark.svg" alt="" />
                    <span>© 2024</span>
                </li>
                <li>
                    <a href="">Accessiblity</a>
                </li>
                <li>
                    <a href="">User Agreement</a>
                </li>
                <li>
                    <a href="">Privacy Policy</a>
                </li>
                <li>
                    <a href="">Cookie Policy</a>
                </li>
                <li>
                    <a href="">Copywright Policy</a>
                </li>
                <li>
                    <a href="">Brand Policy</a>
                </li>
                <li>
                    <a href="">Guest Controls</a>
                </li>
                <li>
                    <a href="">Community Guidelines</a>
                </li>
                <li>
                    <a href="">Language</a>
                </li>
            </ul>

        </footer>
    </div>
  )
}

export default AuthenticationLayout