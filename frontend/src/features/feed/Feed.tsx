import { useNavigate } from "react-router-dom";
import Button from "../../components/Button/Button";
import { useAuthentication } from "../authentication/contexts/AuthenticationContextProvider";
import LeftSideBar from "./components/LeftSidebar/LeftSideBar";
import RightSidebar from "./components/RightSidebar/RightSidebar";
import classes from "./Feed.module.scss";
import { useEffect, useState } from "react";
import { Post, Posts } from "./components/Post/Post";
import Modal from "./components/Modal/Modal";

export default function Feed() {
  const { user, logout } = useAuthentication();
  const [feedContent, setFeedContent] = useState<"all" | "connections">(
    "connections"
  );
  const navigate = useNavigate();
  const [showPostingModal, setShowPostingModal] = useState(false);
  const [posts, setPosts] = useState<Post[]>([]);
  const [error, setError] = useState("");

  useEffect(() => {
    const fetchPosts = async () => {
      try {
        const response = await fetch(
          import.meta.env.VITE_API_URL +
            "/api/v1/feed" +
            (feedContent === "connections" ? "" : "/posts"),
          {
            headers: {
              Authorization: `Bearer ${localStorage.getItem("token")}`,
            },
          }
        );

        if (!response.ok) {
          const { message } = await response.json();
          throw new Error(message);
        }
        const data = await response.json();
        setPosts(data);
      } catch (e) {
        if (e instanceof Error) {
          setError(e.message);
        } else {
          setError("An error occurred, Please try again later");
        }
      }
    };

    fetchPosts();
  }, [feedContent]);
  return (
    <div className={classes.root}>
      <div className={classes.left}>
        <LeftSideBar />
      </div>
      <div className={classes.center}>
        <div className={classes.posting}>
          <button
            onClick={() => {
              navigate(`/profile/${user?.id}`);
            }}
          >
            <img
              className={`${classes.top} ${classes.avatar}`}
              src={user?.profilePicture || "/avatar.svg"}
              alt=""
            />
          </button>
          <Button outline onClick={() => setShowPostingModal(true)}>
            Start a post
          </Button>
          <Modal
            title="Creating a post"
            onSubmit={async () => {}}
            showModal={showPostingModal}
            setShowModal={setShowPostingModal}
          />
        </div>
        {/* <div className={classes.feed}></div> */}
        {error && <div className={classes.error}> {error}</div>}
        <div className={classes.header}>
          <button
            className={feedContent === "all" ? classes.active : ""}
            onClick={() => setFeedContent("all")}
          >
            All
          </button>

          <button
            className={feedContent === "connections" ? classes.active : ""}
            onClick={() => setFeedContent("connections")}
          >
            Feed
          </button>
        </div>

        <div className={classes.feed}>
          {posts.map((post) => (
            <Posts key={post.id} post={post} setPosts={setPosts} />
          ))}
        </div>
      </div>
      <div className={classes.right}>
        <RightSidebar />
      </div>
    </div>
  );
}
