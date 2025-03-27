import { useEffect, useState } from "react";
import { request } from "../../../../utils/api";
import { Post, Posts } from "../../components/Post/Post";
import { useParams } from "react-router-dom";
import LeftSideBar from "../../components/LeftSidebar/LeftSideBar";
import RightSidebar from "../../components/RightSidebar/RightSidebar";
import classes from "./Post.module.scss"
import { useAuthentication } from "../../../authentication/contexts/AuthenticationContextProvider";

function PostPage() {
  const [post, setPost] = useState<Post | null>(null);
  const { id } = useParams();
  const { user } = useAuthentication();
  useEffect(() => {
    request<Post>({
      endpoint: `/api/v1/feed/posts/${id}`,
      onSuccess: setPost,
      onFailure: (error) => console.log(error),
    });
  }, [id]);

  return (
    <div className={classes.root}>
      <div className={classes.left}>
        <LeftSideBar user={user}/>
      </div>
      <div className={classes.center}>{post && <Posts post={post} />}</div>
      <div className={classes.right}>
        <RightSidebar />
      </div>
    </div>
  );
}

export default PostPage;
