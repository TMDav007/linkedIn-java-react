import classes from "./Input.module.scss";
import { InputProps } from "./InputProps";


function Input({ label, size, width, ...otherProps }: InputProps) {
  return (
    <div className={`${classes.root} ${classes[size || "large"]}`}>
      {label ? (
        <label className={classes.label} htmlFor={otherProps.id}>
          {label}
        </label>
      ) : null}
      <input {...otherProps} style={{ width: width ? `${width}px` : "100%" }} />
    </div>
  );
}

export default Input;
