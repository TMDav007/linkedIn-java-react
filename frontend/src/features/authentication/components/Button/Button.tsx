import React, { ButtonHTMLAttributes, InputHTMLAttributes } from 'react'
import classes from "./Button.module.scss"

 type ButtonProps = ButtonHTMLAttributes<HTMLButtonElement> & {
    outline?: boolean;
};

function Button({outline,children, ...other}: ButtonProps) {
  return (
    <button {...other} className={`${classes.root} ${outline? classes.outline : ""}`} >
        {children}
    </button>
  )
}

export default Button