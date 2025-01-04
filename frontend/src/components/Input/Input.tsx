import React, { InputHTMLAttributes } from "react";
import classes from "./Input.module.scss";

type InputProps = InputHTMLAttributes<HTMLInputElement> & {
  label?: string;
  size?: "small" | "medium" | "large";
};
function Input({ label, size, width, ...otherProps }: InputProps) {
  return (
    <div className={`${classes.root} ${classes[size || "large"]}`}>
      {label ? (
        <label className={classes.label} htmlFor={otherProps.id}>
          {label}
        </label>
      ) : null}
      <input {...otherProps} 
      style={{width: width? `${width}px` : "100%" }}
      />
    </div>
  );
} 

export default Input;
