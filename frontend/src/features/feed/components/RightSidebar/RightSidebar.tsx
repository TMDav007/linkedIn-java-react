import React from "react";
import classes from "./RightSidebar.module.scss";
import Button from "../../../../components/Button/Button";

export default function RightSidebar() {
  return (
    <div className={classes.root}>
      <h3>Add to your connexions</h3>
      <div className={classes.items}>
        {/* {suggestions.map((suggestion) => { */}
          {/* return ( */}
            <div className={classes.item} >
              <img
                src={"https://i.pravatar.cc/300"}
                alt=""
                className={classes.avatar}
              />
              <div className={classes.content}>
                <div className={classes.name}>
                  Annis Doe
                </div>
                <div className={classes.title}>
                  HR at Spotify
                </div>
                <Button
                  size="medium"
                  outline
                  className={classes.button}
                  onClick={() => {
                    // request<IConnection>({
                    //   endpoint:
                    //     "/api/v1/networking/connections?recipientId=" +
                    //     suggestion.id,
                    //   method: "POST",
                    //   onSuccess: () => {
                    //     setSuggestions(
                    //       suggestions.filter((s) => s.id !== suggestion.id)
                    //     );
                    //   },
                    //   onFailure: (error) => console.log(error),
                    // });
                  }}
                >
                  + Follow
                </Button>
              </div>
            </div>
          {/* ); */}
          
        {/* })} */}

        {/* {suggestions.length === 0 && (
          <div className={classes.empty}>
            <p>No suggestions at the moment :(</p>
          </div> 
        )} */}
      </div>
    </div>
  );
}
