import React, { ButtonHTMLAttributes, InputHTMLAttributes } from "react";
import classes from "./Button.module.scss";

type ButtonProps = ButtonHTMLAttributes<HTMLButtonElement> & {
  outline?: boolean;
  size?: "small" | "medium" | "large";
};

function Button({
  outline,
  size = "large",
  className,
  children,
  ...other
}: ButtonProps) {
  return (
    <button
      {...other}
      className={`${classes.root} ${outline ? classes.outline : ""} ${
        classes[size]
      } ${className}`}
    >
      {children}
    </button>
  );
}

export default Button;
