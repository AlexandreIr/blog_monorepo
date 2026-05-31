import { Link } from "react-router-dom";
// @ts-ignore
import "./header.css";
import React from "react";
import {isAuthenticated} from "../../api/adminApi";
import {ArrowLeftIcon} from "lucide-react";

interface HeaderProps {
  search: string;
  onSearchChange: (value: string) => void;
  onSearchSubmit?: (event: React.FormEvent<HTMLFormElement>) => void;
}

export function Header({ search, onSearchChange, onSearchSubmit }: HeaderProps) {
  return (
    <header>

      {isAuthenticated() && (
          <div className="admin-area">
            <ArrowLeftIcon />
            <Link to="/painel-secreto">Voltar para o painel administrativo</Link>
          </div>
      )}

      <div className="navbar">
        <Link to="/" className="logo-area">
          <img src="/logo-header.png" alt="libertad-logo"/>
        </Link>

        <nav>
          <Link className="active" to="/">Blog</Link>
          <a>Sobre</a>
          <a>Serviços</a>
          <a>Contato</a>
        </nav>

        <form className="search-box" onSubmit={onSearchSubmit}>
          <input
            placeholder="Buscar no blog..."
            value={search}
            onChange={(e) => onSearchChange(e.target.value)}
          />
        </form>
      </div>
    </header>
  );
}