import { PublicationComment } from './publication-comment.model';
import { PublicationLike } from './publication-like.model';

export class InteractivePublication {
  ipublicationId!: number;
  title!: string;
  description!: string;
  publicationStatus!: string;
  publicationVisibility!: string;
  publicationModerationStatus!: string;
  publicationDate!: string;
  interactionCount!: number;
  scheduledPublishTime!: string;
  recommendationScore!: number;
  userId!: number;

  // New fields for likes and comments
  likeCount: number = 0;
  commentCount: number = 0;
  userHasLiked: boolean = false;
  comments: PublicationComment[] = [];
  likes: PublicationLike[] = [];
  showComments: boolean = false;
}
