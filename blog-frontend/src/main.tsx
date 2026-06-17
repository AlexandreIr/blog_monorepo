import React from "react";
import ReactDOM from "react-dom/client";
import { BrowserRouter } from "react-router-dom";
import App from "./App";
// @ts-ignore
import "./style.css";
import {ThemeProvider} from "./context/ThemeContext";

// @ts-ignore
ReactDOM.createRoot(document.getElementById("root")).render(
  <BrowserRouter>
    <ThemeProvider>
        <App />
    </ThemeProvider>
  </BrowserRouter>
);