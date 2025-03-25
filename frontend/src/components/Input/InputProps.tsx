import { InputHTMLAttributes } from "react";

export interface InputProps extends Omit<InputHTMLAttributes<HTMLInputElement>, "size">  {
  label?: string;
  size?: "small" | "medium" | "large"
};

