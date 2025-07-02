import React, { useEffect, useState } from "react";
import classes from "./RightSidebar.module.scss";
import Button from "../../../../components/Button/Button";
import { User } from "../../../authentication/contexts/AuthenticationContextProvider";
import { request } from "../../../../utils/api";
import { IConnection } from "../../../networking/components/Connection/Connection";
import { useNavigate, useParams } from "react-router-dom";
import Loader from "../../../../components/Loader/Loader";

export default function RightSidebar() {
  const [suggestions, setSuggestions] = useState<User[]>([]);
  const navigate = useNavigate();
  const { id } = useParams();
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    request<User[]>({
      endpoint: "/api/v1/networking/suggestions?limit=2",
      onSuccess: (data) => {
        const shuffled = data.sort(() => 0.5 - Math.random());
        setSuggestions(shuffled.slice(0, 2));
        if (id) {
          setSuggestions(data.filter((s) => s.id !== id));
        } else {
          setSuggestions(data);
        }
      },
      onFailure: (error) => console.log(error),
    }).then(() => setLoading(false));
  }, [id]);

  return (
    <div className={classes.root}>
      <h3>Add to your connexions</h3>
      <div className={classes.items}>
        {/* {suggestions.map((suggestion) => { */}
        {/* return ( */}
        {suggestions.map((suggestion) => {
          return (
            <div className={classes.item} key={suggestion.id}>
              <button
                className={classes.avatar}
                onClick={() => navigate("/profile/" + suggestion.id)}
              >
                <img src={suggestion.profilePicture || "/avatar.svg"} alt="" />
              </button>
              <div className={classes.content}>
                <button onClick={() => navigate("/profile/" + suggestion.id)}>
                  <div className={classes.name}>
                    {suggestion.firstName} {suggestion.lastName}
                  </div>
                  <div className={classes.title}>
                    {suggestion.position} at {suggestion.company}
                  </div>
                </button>
                <Button
                  size="medium"
                  outline
                  className={classes.button}
                  onClick={() => {
                    request<IConnection>({
                      endpoint:
                        "/api/v1/networking/connections?recipientId=" +
                        suggestion.id,
                      method: "POST",
                      onSuccess: () => {
                        setSuggestions(
                          suggestions.filter((s) => s.id !== suggestion.id)
                        );
                      },
                      onFailure: (error) => console.log(error),
                    });
                  }}
                >
                  + Connect
                </Button>
              </div>
            </div>
          );
        })}

        {suggestions.length === 0 && !loading && (
          <div className={classes.empty}>
            <p>No suggestions available at the moment </p>
          </div>
        )}
        {loading && <Loader isInline />}
      </div>
    </div>
  );
}
