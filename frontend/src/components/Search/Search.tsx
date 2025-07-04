import React, { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom';
import { request } from '../../utils/api';
import classes from "./Search.module.scss";
import { User } from '../../features/authentication/contexts/AuthenticationContextProvider';
import Input from '../Input/Input';

function Search() {

 const [searchTerm, setSearchTerm] = useState("");
  const [suggestions, setSuggestions] = useState<User[]>([]);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchSuggestions = async () => {
      if (searchTerm.length > 0) {
        request<User[]>({
          endpoint: "/api/v1/search/users?query=" + searchTerm,
          onSuccess: (data) => setSuggestions(data),
          onFailure: (error) => console.log("Search error:", error),
        });
      } else {
        setSuggestions([]);
      }
    };
    fetchSuggestions();
    // const delayDebounceFn = setTimeout(fetchSuggestions, 300);
    // return () => clearTimeout(delayDebounceFn);
  }, [searchTerm]);

  return (
    <div className={classes.search}>
      <Input
        onChange={(e) => setSearchTerm(e.target.value)}
        placeholder="Search for connections"
        size="medium"
        value={searchTerm}
      />
      {suggestions.length > 0 && (
        <ul className={classes.suggestions}>
          {suggestions.map((user) => (
            <li key={user.id} className={classes.suggestion}>
              <button
                key={user.id}
                onClick={() => {
                  setSuggestions([]);
                  setSearchTerm("");
                  navigate(`/profile/${user.id}`);
                }}
              >
                <img className={classes.avatar} src={user.profilePicture || "/avatar.svg"} alt="" />
                <div>
                  <div className={classes.name}>
                    {user.firstName} {user.lastName}
                  </div>
                  <div className={classes.title}>
                    {user.position} at {user.company}
                  </div>
                </div>
              </button>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}

export default Search
