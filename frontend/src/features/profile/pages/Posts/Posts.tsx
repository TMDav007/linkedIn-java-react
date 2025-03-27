import { useParams } from "react-router-dom";
import classes from "./Posts.module.scss";
import { useEffect, useState } from "react";
import { Posts as Pst, Post } from "../../../feed/components/Post/Post";
import { useAuthentication, User } from "../../../authentication/contexts/AuthenticationContextProvider";
import { usePageTitle } from "../../../../hooks/usePageTitle";
import { request } from "../../../../utils/api";
import Loader from "../../../../components/Loader/Loader";
import LeftSideBar from "../../../feed/components/LeftSidebar/LeftSideBar";
import RightSidebar from "../../../feed/components/RightSidebar/RightSidebar";
 export function Posts() {
   const { id } = useParams();
   const [posts, setPosts] = useState<Post[]>([]);
   const { user: authUser } = useAuthentication();
   const [user, setUser] = useState<User | null>(null);
   const [loading, setLoading] = useState(true);
   usePageTitle("Posts | " + user?.firstName + " " + user?.lastName);
   useEffect(() => {
     if (id == authUser?.id) {
       setUser(authUser);
       setLoading(false);
     } else {
       request<User>({
         endpoint: `/api/v1/authentication/users/${id}`,
         onSuccess: (data) => {
           setUser(data);
           setLoading(false);
         },
         onFailure: (error) => console.log(error),
       });
     }
   }, [authUser, id]);
 
   useEffect(() => {
     request<Post[]>({
       endpoint: `/api/v1/feed/posts/user/${id}`,
       onSuccess: (data) => setPosts(data),
       onFailure: (error) => console.log(error),
     });
   }, [id]);
 
   if (loading) {
     return <Loader />;
   }
   return (
     <div className={classes.posts}>
       <div className={classes.left}>
         <LeftSideBar user={user} />
       </div>
       <div className={classes.main}>
         <h1>{user?.firstName + " " + user?.lastName + "'s posts"}</h1>
         {posts.map((post) => (
           <Pst key={post.id} post={post} setPosts={setPosts} />
         ))}
         {posts.length === 0 && (
           <div className={classes.empty}>
             <p>No post to display.</p>
           </div>
         )}
       </div>
       <div className={classes.right}>
         <RightSidebar />
       </div>
     </div>
   );
 }