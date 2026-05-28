import { Link } from "react-router-dom";
import "./header.css";

interface HeaderProps {
  search: string;
  onSearchChange: (value: string) => void;
  onSearchSubmit?: (event: React.FormEvent<HTMLFormElement>) => void;
}

export function Header({ search, onSearchChange, onSearchSubmit }: HeaderProps) {
  return (
    <header>

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