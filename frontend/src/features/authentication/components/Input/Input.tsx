import React, { InputHTMLAttributes } from 'react'
import classes from "./Input.module.scss"

type InputProps = InputHTMLAttributes<HTMLInputElement> & {
    label: string;
}
function Input({label, ...otherProps}: InputProps) {
  return (
    <div className={classes.root}>
        <label>{label}</label>
        <input {...otherProps}/>
    </div>
  )
}

export default Input