import {ReactNode} from 'react'
import classes from "./Separator.module.scss"

function Separator({
    children
}: {children: ReactNode}) {
  return (
    <div className={classes.root}>{children}</div>
  )
}

export default Separator