import { useNavigate } from "react-router-dom";
import LeftSideBar from "./../../components/LeftSidebar/LeftSideBar";
import RightSidebar from "./../../components/RightSidebar/RightSidebar";
import classes from "./Feed.module.scss";
import { useEffect, useState } from "react";

import Modal from "./../../components/Modal/Modal";
import { Post, Posts } from "../../components/Post/Post";
import { useAuthentication } from "../../../authentication/contexts/AuthenticationContextProvider";
import { request } from "../../../../utils/api";
import Button from "../../../../components/Button/Button";
import { useWebSocket } from "../../../websocket/Ws";
import Loader from "../../../../components/Loader/Loader";

export default function Feed() {
  const { user, logout } = useAuthentication();
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();
  const [showPostingModal, setShowPostingModal] = useState(false);
  const [posts, setPosts] = useState<Post[]>([]);
  const [error, setError] = useState("");

  const ws = useWebSocket();

  useEffect(() => {
    const endpoint = "/api/v1/feed";
    const fetchPosts = async () => {
      await request<Post[]>({
        endpoint,
        onSuccess: (data) => {
          setPosts(data);
          setLoading(false);
        },
        onFailure: (error) => setError(error),
      });
    };

    fetchPosts();
  }, []);

  useEffect(() => {
    const subscription = ws?.subscribe(
      `/topic/feed/${user?.id}/post`,
      (data) => {
        const post = JSON.parse(data.body);
        setPosts((posts) => [post, ...posts]);
      }
    );
    return () => subscription?.unsubscribe();
  }, [user?.id, ws]);

  const handlePost = async (data: FormData) => {
    await request<Post>({
      endpoint: "/api/v1/feed/posts",
      method: "POST",
      contentType: "multipart/form-data",
      body: data,
      onSuccess: (data) => setPosts([data, ...posts]),
      onFailure: (error) => setError(error),
    });
  };

  return (
    <div className={classes.root}>
      <div className={classes.left}>
        <LeftSideBar user={user} />
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
              src={
                user?.profilePicture
                  ? `${import.meta.env.VITE_API_URL}/api/v1/storage/${
                      user?.profilePicture
                    }`
                  : "/avatar.svg"
              }
              alt=""
            />
          </button>
          <Button outline onClick={() => setShowPostingModal(true)}>
            Start a post
          </Button>
          <Modal
            title="Creating a post"
            onSubmit={handlePost}
            showModal={showPostingModal}
            setShowModal={setShowPostingModal}
          />
        </div>
        {/* <div className={classes.feed}></div> */}
        {error && <div className={classes.error}> {error}</div>}

        {loading ? (
          <Loader isInline />
        ) : (
          <div className={classes.feed}>
            {posts.map((post) => (
              <Posts key={post.id} post={post} setPosts={setPosts} />
            ))}
            {posts.length === 0 && (
              <p>
                Start connecting with people to build a feed that matters to
                you.
              </p>
            )}
          </div>
        )}
      </div>
      <div className={classes.right}>
        <RightSidebar />
      </div>
    </div>
  );
}
