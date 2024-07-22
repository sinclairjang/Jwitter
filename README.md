# Jwitter

> 작성자: 장진영(sinclairjang@gmail.com)
> 
> 원문: https://cuboid-tarantula-e0b.notion.site/Jwitter-e1968f7cf2f24369a9832103a7e44816
>
> Github: https://github.com/sinclairjang/Jwitter
> 

> 개요:  트위터(현재 [X](https://x.com/home)) 타임라인의 재구성 (feat. `Spring boot`) 백엔드  프로젝트
> 

## Application design

<aside>
<img src="https://www.notion.so/icons/book-closed_gray.svg" alt="https://www.notion.so/icons/book-closed_gray.svg" width="40px" /> Reference:  https://www.infoq.com/presentations/Twitter-Timeline-Scalability/

</aside>

- About InfoQ
    
    소프트웨어는 세상을 바꾸고 있습니다. QCon은 지식과 혁신의 확산을 통해 엔터프라이즈 소프트웨어 개발 커뮤니티에서 소프트웨어 개발을 강화하려합니다. 이를 달성하기 위해, QCon은 혁신에 영향을 미치는 팀원들: 팀 리더, 아키텍트, 프로젝트 매니저, 엔지니어링 디렉터들을 대상으로 한 실무자 주도의 컨퍼런스로 구성되어 있습니다.
    

---

### Main services

트위터는 사용자가 자신의 꿈, 아이디어, 생각을 다른 사람들과 공유하는 플랫폼으로서의 기능을 주요 서비스로 하고 있습니다. 이를 뒷받침하기 위한 시스템 구조 설계를 담당했던 Raffi Krikorian의 2012년 [QCon영상](https://www.infoq.com/presentations/Twitter-Timeline-Scalability/)에서 제시하고 있는 트위터의 주요 서비스들 중 본 프로젝트에서 구현하는 것은 다음과 같습니다.

1. Followers / following
    
    사용자는 다른 사용자들을 팔로우함으로써 그들의 트윗을 전달받게 됩니다. 반대로 얘기하면 사용자의 트윗은 자신을 팔로우하는 다른 사용자들에게 전달되게 된다는 것을 의미합니다.
    
2. Post tweet : `Produce`
    
    앞서 언급한  ‘공유 플랫폼’의 콘텐츠 생산 측면에서의 서비스에 해당하는 것으로 트윗을 포스트하는 사용자는 언제 어디서나 자신의 꿈, 아이디어, 생각을 세상에 표현할 수 있습니다.
    
3. Home timeline : `Consume`
    
    콘텐츠 소비자 측면에서의 서비스에 해당하는 것으로 자신이 팔로우하고 있는 사용자의 트윗을 실시간으로 전달 받을 수 있습니다.
    
4. User timeline : `Consume`
    
    마찬가지로 콘텐츠 소비자 측면에서의 서비스에 해당하는 것으로 과거 자신이 포스팅했던 트윗들을 전달 받을 수 있습니다.
    

이외에 로그인/로그아웃을 포함한 보안 및 인증과 관련된 기능은 필수 기능이기 때문에 따로 언급하지 않았습니다.

### Load parameters

마찬가지로 Raffi Krikorian이 위 영상에서 제시하고 있는 Load Parameter*(주로 백엔드 시스템에 미치는 부하의 원인을 여러 요인으로 분석한 것)*은 다음과 같습니다.

1. 1억 5천만명 이상의 전세계 활성 사용자
2. 초당 3백만개 이상의 read 요청(Home timeline, User timeline) 
3. 초당 4천개 이상의 write 요청(Post tweet) *피크타임 초당 1만개 이상

### Functional Requirements

1. `/login` URL에서 로그인한 사용자만 서비스 이용이 가능합니다. 
2. `/signup` URL에서 회원가입 신청이 가능합니다. 
    1. 비밀번호와 재확인 비밀번호가 일치해야 합니다.
    2. 영문 아이디만 가능합니다. (최소 2자 최대 48자)
    3. 이메일을 등록해야 합니다.
3. `/home` 트위터 홈도메인에 Home timeline을 스크롤링 방식으로  노출시킵니다.
4. 이 때 트윗은 좋아요가 높고 최근에 작성된 순으로 노출시킵니다.
5. Home timeline의 최대 트윗 개수는 800으로 설정합니다.
6. Home timeline가 최대 트윗 개수가 되었을 때 새로운 트윗 등록을 요청받으면 제일 오래된 트윗을 제거하고 새로운 트윗을 등록합니다.
7. User timeline의 트윗 개수에는 제한이 없으며 마찬가지로 스크롤링 방식으로 트윗을 노출시킵니다.
8. 트윗의 최대 글자 수는 280자로 제한합니다.
9. 사용자는 자신이 작성한 트윗에 대해서 수정/삭제를 할 수 있습니다.
10. 다른 사람이 작성한 트윗에 ‘좋아요’를 하거나 댓글을 달 수 있습니다.
11. 다른 사람의 댓글에 대해서 ‘좋아요’를 하거나 댓글을 달 수 있습니다.
12. 자신이 현재 팔로우하고 있는 사용자 목록과 자신을 팔로하우하고 있는 사용자의 목록을 조회할 수 있습니다.

### Non-functional requirements

1. 개인정보 보호
2. 낮은 지연시간
3. 부적절한 트윗 검열
4. 높은 가용성 및 확장성

### API design

1. Social

| API | Detail |
| --- | --- |
| GET /v1/following/:id | 현재 자신이 팔로우하고 있는 사용자의 목록을 조회 |
| GET /v1/followers/:id | 현재 자신을 팔로우하고 있는 사용자의 목록을 조회 |
| POST /v1/follow/:id | 해당 사용자를 팔로우 |
1. Tweet

| API | Detail |
| --- | --- |
| GET /v1/tweet/:id | 트윗 조회 |
| POST /v1/tweet/:id | 트윗 작성 |
| PUT /v1/tweet/:id | 트윗 수정 |
| DELETE /v1/tweet/:id | 트윗 삭제 |
| POST /v1/tweet/:id/like | 해당 트윗에 ‘좋아요’ 하기 |
| POST /v1/tweet/:id/comment | 해당 트윗에 댓글 쓰기 |
1. Timeline

***GET** /v1/home_timeline*

위 엔드-포인트는 홈 타임라인을 조회합니다. 단, 한 뷰포트내에 보여질 수 있는 트윗의 개수는 ~10개 미만이므로 최대 800개의 트윗을 요청하면 자원 낭비가 되기 때문에 pagination을 해야합니다. 

**Pagination API design**

![출처: [https://bytebytego.com](https://bytebytego.com/)](Jwitter%20e1968f7cf2f24369a9832103a7e44816/pagination.gif)

출처: [https://bytebytego.com](https://bytebytego.com/)

트위터는 스크롤링 방식으로 홈 타임라인을 구성하기 때문에 Cursor-Based 방식이 적절해보입니다.

*Request Parameters:*

| Field | Description | Type |
| --- | --- | --- |
| cursor | 사용자의 커서 위치와 데이터베이스 페이지를 연결하는 고유 식별자(UID) | int |

***GET** /v1/user_timeline*

최대 트윗 개수의 용량적 제한이 없다는 것을 제외하면 타임라인과 서비스 구현 방식이 동일합니다. 단, 단위 시간당 또는 하루에 게시할 수 있는 트윗은 제한이 있을 수 있습니다.

### *Jwitter v0*

1. Read/write ratio

[Load parameter](https://www.notion.so/Jwitter-e1968f7cf2f24369a9832103a7e44816?pvs=21)에 근거해 r/w 배수를 계산해보았습니다.

| 백분위 | r/w 배수 |
| --- | --- |
| p50 | 3,000,000 / 4,000 = 750 |
| p99 | 3,000,000 / 10,000 = 300 |
1. Data modeling
    1. Entity Relatoin Diagram(ERD)
    
    ![jwitter_erd.svg](Jwitter%20e1968f7cf2f24369a9832103a7e44816/jwitter_erd.svg)
    
    b. Java Persistence Object (implemented in Spring)
    
    ```java
    @Entity
    @Table(name = "USERS")
    public class User {
    	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    	private Long id;
    	private String username;
    	private String password;
    	
    	@OneToMany(mappedBy = "follower", fetch = FetchType.LAZY)
    	private Set<Follow> followings = new HashSet<>();
    	@OneToMany(mappedBy = "following", fetch = FetchType.LAZY)
    	private Set<Follow> followers = new HashSet<>();
    	@OneToMany(mappedBy = "sender", fetch = FetchType.LAZY)
    	private Set<Tweet> tweets = newHashSet<>();
    	
    	// 생성자 구현
    }
    
    @Entity
    @Table(name = "TWEETS")
    public class  Tweet {
    	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    	private Long id;
    	private String text;
    	@UpdateTimestamp
    	private DateTime timeStamp;
    	
    	@ManyToOne
    	@JoinColumn(name = "sender_id")
    	private User sender;
    	
    	// 생성자 구현
    }
    
    @Entity
    @Table(name = "FOLLOWS")
    @org.hibernate.annotations.Immutable
    public class Follow {
    	@Embeddable
    	public static class Id implements Serializable {
    		@Column(name = "follower_id")
    		private Long followerId;
    		@Column(name = "following_id")
    		private Long followingId;
    		
    		public Id() {}
    		public Id(Long followerId, Long followingid) {
    			this.followerId = follwerId;
    			this.follwingId = follwingId;
    		}
    		
    		// equals, hashCode 구현(ORM impedance mismatch 방지)
    	} 
    	
    	@EmbeddedId
    	private Id id = new Id();
    	
    	@ManyToOne
    	@JoinColumn(
    		name = "follower_id",
    		insertable = false, updatable = false)
    	private User follower;
    	
    	@ManyToOne
    	@JoinColumn(
    		name = "following_id",
    		insertable = false, updatable = false)
    	private User following;
    	
    	// 생성자 구현
    }
    
    ```
    
    <aside>
    ❗ `User` 에 맵핑되는 `follower`와 `following` 를 읽기 전용으로 설정하지 않으면 `@EmbeddedId`와 중복 칼럼 문제를 일으킵니다.
    
    </aside>
    
    c. Query example
    
    ```java
    // Home timeline 
    User user = userRepository.findById(...);
    Set<Follow> followings = user.getFollowings();
    for (Follow follow : followings) {
    	User following = follow.getFollowing();
    	Set<Tweet> tweets = following.getTweets();
    	for (Tweet tweet : tweets) {
    		doSomethingWith(tweet.text);
    	}
    	
    }
    ---------------------------------------------
    // Post tweet
    User user = userRepository.findById(...);
    Tweet tweet = new Tweet("some text", user);
    tweetRepository.save(tweet); 
    ```
    
    Post tweet query는 $O(1)$의 복잡도를 갖으므로 확장성에 문제가 되지 않습니다. 
    
    반면 Home timeline query는 어떨까요? 🤔
    
     d. Home timeline query performance analysis
    
    ---
    
    > #Issue: N+1 selects problem
    > 
    > 
    > **select** follows.following_id
    > **from** follows 
    > **where** follows.follower_id = ?
    > 
    > **select** tweet.text
    > 
    > **from** tweets
    > 
    > **where** tweets.sender_id = ?
    > 
    > **select** tweet.text
    > 
    > **from** tweets
    > 
    > **where** tweets.sender_id = ?
    > 
    > **select** tweet.text
    > 
    > **from** tweets
    > 
    > **where** tweets.sender_id = ?
    > 
    > .
    > 
    > .
    > 
    > .
    > 
    > (N번 반복)
    > 
    > Cost: 알고리즘 복잡도 $O(N^2)$ + 데이터베이스 서버 N+1 Round-trip  —>  ~~확장성~~ 
    > 
    
    Sprng JPA 또는 Hibernate는 `EntityManager` 오브젝트를 통해 Java Persistence Object인 Entity를 관리합니다. `EntityManager` 는 자체적으로 `find({Entity 클래스}, ...)` 라는 함수를 통해 Entity를 페칭하는 메커니즘으로서 크게 3가지(***SELECT***, ***SUBSELECT***, ***JOIN***) *fetching strategy*를 제공합니다.  위와 같은 N+1 이슈는 바로 Hibernate의 디폴트 페칭 전략인 SELECT에서 비롯된 것입니다.
    

![SELECT(N+1 queries)](Jwitter%20e1968f7cf2f24369a9832103a7e44816/Untitled.png)

SELECT(N+1 queries)

루트 엔티티에 대한 하나의 쿼리 + 각 루트 엔티티의 관련 매핑된 엔티티/컬렉션에 대한 하나의 쿼리로서 가장 *lazy*한 *aceess pattern*입니다.

![SUBSELECT(2 queries)](Jwitter%20e1968f7cf2f24369a9832103a7e44816/Untitled%201.png)

SUBSELECT(2 queries)

첫 번째 쿼리로 루트 엔티티 조회 + 두 번째 쿼리로 첫 번째 쿼리에서 조회된 모든 루트 엔티티와 관련된 매핑된 엔티티/컬렉션 조회로서 양 극단의 *SELECT*, *JOIN* 사이의 중간단계의 *access pattern*입니다.

![JOIN(1 query)](Jwitter%20e1968f7cf2f24369a9832103a7e44816/Untitled%202.png)

JOIN(1 query)

루트 엔터티와 모든 매핑된 엔터티/컬렉션을 가져오는 하나의 쿼리로서 가장 *eager*한 *access pattern*입니다. 

e. 해결방안?

```sql
[SQL]
-------------------------------------------------
SELECT tweets.*, users.* FROM tweets
	JOIN users   ON tweets.sender_id     = users.id
	JOIN follows ON follows.following_id = users.id
	WHERE follows.follower_id = current_user
```

위 SQL문을 JPQL, CriteriaQuery, JDBC 등에 맵핑해서 JOIN 메커니즘을 구현하면 당장 N+1 문제는 해결할 수 있습니다. 다만 앞서 살펴봤던 [Load Parameters](https://www.notion.so/Jwitter-e1968f7cf2f24369a9832103a7e44816?pvs=21) 타임라인 쿼리에 의한 부하를 감당할 수 있을지 더 깊이 고민해봐야합니다. 그러기 위해서는 SQL이 내부적으로 JOIN문을 어떻게 처리하는지 들여다봐야 합니다.

f. SQL Joins internals

<aside>
<img src="https://www.notion.so/icons/book-closed_gray.svg" alt="https://www.notion.so/icons/book-closed_gray.svg" width="40px" /> Reference: SQL Performance Explained (Markus Winand, 2012)

</aside>

![[https://bertwagner.com](https://bertwagner.com/)](Jwitter%20e1968f7cf2f24369a9832103a7e44816/Nested-Loop-Join-50fps-1.gif)

[https://bertwagner.com](https://bertwagner.com/)

다른 알고리즘에 비해 가장 간단하면서 따로 전처리 작업이나 관리해야할 자료구조가 필요 없기 때문에 적은 양의 데이터에 대해서 SQL Query Optimizer가 선호하는 알고리즘입니다.

참고로 앞서 살펴본 Hibernate의 SELECT 페칭전략이 어플리케이션 단에서 구현하는 알고리즘이기도 합니다.

테이블 A의 각 레코드에 대해서 테이블 B의 모든 레코드를 순회하며 비교하기 때문에 총 시간 복잡도는 $O(N^2)$입니다.

---

![[https://bertwagner.com](https://bertwagner.com/)](Jwitter%20e1968f7cf2f24369a9832103a7e44816/Merge-Join-1.gif)

[https://bertwagner.com](https://bertwagner.com/)

병합 조인을 위해 테이블 A와 테이블 B의 인덱스는 이미 정렬된 경우가 아니면 임시로 정렬되며 개별 포인터가 각 테이블의 상단에 배치됩니다. 

정렬된 인덱스 속성을 이용해서 다른 테이블의 레코드를 앞서나갈 때 까지 포인터를 아래로 이동시키는 과정을 반복하는 알고리즘입니다.

이미 정렬되어 있는 테이블에 대해서 선호되는 알고리즘입니다.

정렬하는 시간을 포함해 총 시간 복잡도는 $O(N*logN + N)$ 입니다.

---

![[https://bertwagner.com](https://bertwagner.com/)](Jwitter%20e1968f7cf2f24369a9832103a7e44816/Hash-Match-Join-Looping-1.gif)

[https://bertwagner.com](https://bertwagner.com/)

Nested Loop Join의 중첩순회를 없애고  첫번째테이블의 인덱스를 해시테이블에서 저장해두었다가 두번째 테이블을 순회할 양 인덱스를 비교하는 방식입니다. 

다만 테이블의 크기가 커지면 해시테이블이 차지하는 메모리가 커지게 되므로 메모리 용량을 초과하지 않도록 주의해야합니다.

위와 같은 경우가 아닐 때 일반적으로 선호되는 알고리즘입니다.

총 시간 복잡도는 $O(N)$ 입니다.

g. 결론

이렇게 SQL의 Join문 처리방식을 배워보면서 시간 복잡도만을 따져봤을 때 *Jwitter v0* Home timeline query의 **최고의 퍼포먼스로 $O(N)$을 기대할 수 있다는 것을 알게되었습니다. 이 추정치는 사업단에서 매출액, 소비자 만족도 등에 영향을 주는 두 SLIs(Service Level Indicators) 지표에 직결됩니다.

1. Response time : 실제 요청 처리 시간 + 네트워크 지연 및 대기열 지연 시간
2. Latency : 대기열 지연 시간

![[Queueing by Sam Rose](https://encore.dev/blog/queueing?utm_source=newsletter.programmingdigest.net&utm_medium=newsletter&utm_campaign=queueing)](Jwitter%20e1968f7cf2f24369a9832103a7e44816/queue.gif)

[Queueing by Sam Rose](https://encore.dev/blog/queueing?utm_source=newsletter.programmingdigest.net&utm_medium=newsletter&utm_campaign=queueing)

위 두 지표를 향상시키기 위해서 시스템 확장을 해야하는데 그 방식을 두가지 축으로 구분할 수 있습니다.

1. 수직적 확장: 시스템 하드웨어(CPU, 메모리)의 성능을 향상해 **실제 요청 처리 시간**을 단축하는데 효과적
2. 수평적 확장: 요청을 처리할 수 있는 시스템 개수를 늘림으로써 **대기열 지연 시간**을 단축하는데 효과적

![âPngtreeâsports car clipart cartoon red_5989018.png](Jwitter%20e1968f7cf2f24369a9832103a7e44816/aPngtreeasports_car_clipart_cartoon_red_5989018.png)

> 
> 

![4984.png](Jwitter%20e1968f7cf2f24369a9832103a7e44816/4984.png)

<aside>
❗ 수직적 확장과 수평적 확장은 상호 배타적인 개념이 아니라 확장성을 위해 협업하는 관계입니다.

</aside>

### *Jwitter* *v1*

**System re-design**

Jwitter에서 현재 제일 많은 시스템 부하(초당 3백만개 이상의 read 요청)가 발생하는 [타임라인 서비스](https://www.notion.so/Jwitter-e1968f7cf2f24369a9832103a7e44816?pvs=21)에 대응하기 위해서 우리는 현재 시스템에 구조적인 변화를 주어야 합니다. 

1. 상대적으로 적은 부하(초당 4천개 이상의 write 요청)가 발생하는 [트윗 포스팅](https://www.notion.so/Jwitter-e1968f7cf2f24369a9832103a7e44816?pvs=21) 서비스에 타임라인 서비스의 복잡한 작업을 이전시킴으로써 실제 요청 처리 시간을 단축합니다. 
2. 수평적 확장에 용이한 NoSQL 데이터베이스인 Redis를 클러스터링해서 사용해 대기열 지연 시간 단축을 단축합니다.

먼저 첫번째 구조적 변화에 대한 근거는 콘텐츠 생산보다 소비에 더 치우친 Jwitter의 사용자 이용패턴이 반영된 >500의 [read-write ratio](https://www.notion.so/Jwitter-e1968f7cf2f24369a9832103a7e44816?pvs=21)을 들 수 있습니다. 즉, 서버에서 트윗 포스팅을 하나 처리하고 있을 때 500개의 타임라인 요청을 같이 처리해야 함을 의미합니다. 그런데도 현재 구조에서 포스팅 서비스의 시간복잡도는 $O(1)$인 반면에 홈 타임라인 서비스는 $O(N)$으로 더 큰 시간복잡도를 가지고 있습니다. 이러한 비효율성을 해소하기 위해서는 write-time에서 이전 방식과는 거꾸로 $O(N)$의 복잡한 작업을 미리 해두고 read-time에서 $O(1)$으로 간단하게 불러올 수 있도록 해야합니다.

두번째 변화의 근거는 관계형 데이터베이스의 ACID 보장에 기인한 수평적 확장의 복잡성에 따른 시간적 비효율성에 있습니다. 홈 타임라인 서비스는 영화 좌석 예매, 전자상거래 등과 같은 엄격한 데이터 일관성이 필요하지 않기 때문에 일관성을 엄격하게 지키기 위해 쓰이는 시간적 비용을 절약하는 것이 좋습니다.

| SQL에서의 수평 확장의 복잡성 |
| --- |
| 1. 분산 시스템에서의 ACID 준수 |
| 2. 분산 병합의 복잡성 (Cross-shard joins) |
| 3. 샤드 간 트랜젝션 (Cross-shard transaction) |
| 4. 데이터 일관성 보장 (Synchronization) |
| 5. 샤딩의 함정 (Data distribution, shard key selection, resharding) |
| 6. 다중 서버에서 데이터 무결성 보장 (Foreign/Unique key constraints, cascading) |
| 참조: https://www.designgurus.io/blog/horizontally-scale-sql-databases |

그렇다면 데이터 일관성을 느슨하게 지킨다는 것은 무슨 의미일까요? 

이 물음에 대한 답은 [NoSQL](https://en.wikipedia.org/wiki/NoSQL)  [BASE](https://en.wikipedia.org/wiki/Eventual_consistency) 철학에서 찾을 수 있습니다.

- **Basically available**: 데이터베이스 클러스터의 모든 노드를 사용하기 때문에 읽기 및 쓰기 작업은 가능한 한 많이 할 수 있으나 일관성이 없을 수 있음
- **Soft-state**: 일관성이 보장되지 않기 때문에  읽기 및 쓰기 작업 후에도 일정 시간 반영이 안 되어 있을 가능성이 있음
- **Eventually consistent**: 일부 쓰기를 실행한 후 충분한 시간이 지나면 작업 결과가 반영이 되어 새로운 쓰기가 실행되기 전까지 매 읽기에 대해서 해당 데이터 항목의 동일한 값이 반환됩니다.

이를 도식으로 나타내면 다음과 같습니다.

**Trade-offs**

![[Tradeoffs - ByteByteGo](https://bytebytego.com/)](Jwitter%20e1968f7cf2f24369a9832103a7e44816/cap.png)

[Tradeoffs - ByteByteGo](https://bytebytego.com/)

<aside>
<img src="https://www.notion.so/icons/bookmark_gray.svg" alt="https://www.notion.so/icons/bookmark_gray.svg" width="40px" /> **Partition tolerance**: 데이터베이스 노드 사이의 연결에 장애(정전, 패킷 손실 등)가 생겨도 서비스는 지속됨
**Consistency**: 데이터베이스 클러스터 내 데이터 항목이 동일하기 때문에 어떤 노드에서 데이터를 읽어들여도 상관없음
**Availability**: 클러스터 내 일부 노드가 다운되어도 서비스는 지속됨

</aside>

위 상충관계 의사결정 구조에 따라 Jwitter v1의 홈 타임라인 쿼리 최적화를 다음과 같이 설명할 수 있습니다.

홈 타임라인 쿼리에서의 일관성이란 

1. **어떤 시점에서든** 팔로잉 하는 사용자의 최신 트윗이 모든 팔로워 홈 타임라인에 반영됨
2. **어떤 시점에서든** 팔로잉 하는 사용자의 트윗 업데이트(수정, 삭제)가 모든 홈 타임라인에 반영됨

아래 그림은 위 일관성이 깨지는 시나리오를 나타내고 있습니다. 아래 예시에서 보여지는 일관성이 깨진 원인에는 Elon Musk의 트윗 포스팅 또는 업데이트(수정, 삭제) 요청에 대해 Leader가 네트워크 파티션 문제든지 아니면 단순 네트워크 속도의 차이든지 어떤 이유에서 Follwer 1에 비해 Follower 2에 리플리케이션 요청이 늦게 당도하는 바람에 Alice와 Bob의 타임라인에 차이가 생기며 위 ‘어떤 시점에서든’의 가정이 깨지게 되었습니다.

그런데 전자상거래 서비스라면 이런 차이가 심각한 문제를 야기할 수 있겠지만 엔터테인먼트에 가까운 Jwitter의 홈 타임라인 서비스는 이 정도의 일관성이 깨지는 것을 용인할 수 있다고 판단합니다. 하지만 위 상충관계 의사결정구조에서 보여지듯 그 대가로 서비스 응답시간과 가용성을 높이는 것이 사용자의 만족도 향상 면에서 더 중요하고 필요한 결정이라고 생각합니다.

![Untitled](Jwitter%20e1968f7cf2f24369a9832103a7e44816/Untitled%203.png)

[위 System re-design](https://www.notion.so/Jwitter-e1968f7cf2f24369a9832103a7e44816?pvs=21)을 반영한 타임라인 서비스 메커니즘은 다음과 같습니다. [(전(前)과 비교하기)](https://www.notion.so/Jwitter-e1968f7cf2f24369a9832103a7e44816?pvs=21)

**Architecture**

![Untitled](Jwitter%20e1968f7cf2f24369a9832103a7e44816/Untitled%204.png)

Home timeline

- 사용자가 트윗을 게시하면 해당 사용자를 팔로우하는 모든 사람을 검색합니다.
- 그렇게 검색된 모든 팔로워의 각 홈 타임라인 캐시에 새 트윗을 삽입합니다.
- 홈 타임라인 읽기 요청은 결과가 미리 계산되므로 비용이 저렴합니다.
- 위와 같은 방식으로 각 사용자의 홈 타임라인에 대한 캐시를 트윗 사서함과 같이 유지합니다.

User timeline

- 사용자가 트윗을 게시하면 해당 테이블에 영속적으로 저장합니다.
- 유저 타임라인 읽기 요청은 자신이 게시했던 트윗에 대한 단순한 paginated read 요청이므로 저렴합니다.

**Key-value data modeling**

![tweet_svg.svg](Jwitter%20e1968f7cf2f24369a9832103a7e44816/tweet_svg.svg)

![home_timeline.svg](Jwitter%20e1968f7cf2f24369a9832103a7e44816/home_timeline.svg)

Redis의 Key-value 데이터 모델과 앞서 보았던 [관계형 데이터베이스 엔티티 관계 모델](https://www.notion.so/Jwitter-e1968f7cf2f24369a9832103a7e44816?pvs=21)의 가장 큰 내부적인 차이는 다음과 같습니다.

- 전자는 해시에 기반한 인-메모리형 자료구조이고 후자는 B-Tree 알고리즘에 기반한 디스크형 자료구조임
- 단일 데이터 항목에 대해서 서치를 할 때 전자는 메모리의 임의 접근 기능을 적극 활용해 $O(1)$에 가까운 성능을 보이지만 수용할 수 있는 메모리 용량은 디스크에 비해 적고 시스템이 종료되면 저장하고 있던 데이터를 잃게 됨
- 후자는 블록 단위로 데이터 접근을 용이하게 하는 B-Tree의 특성을 활용해 디스크 메모리에 순차 접근을 통한 $O(logN)$의 최적화된 성능을 보여줌
- 후자는 전자보다 속도는 느리지만 수용할 수 있는 메모리 용량이 더 많고 데이터를 영속적으로 보관할 수 있음

**Relevancy modeling**

[**Functional requirements**](https://www.notion.so/Jwitter-e1968f7cf2f24369a9832103a7e44816?pvs=21)에 따라 좋아요가 높고 최근에 작성된 순으로 정렬된 800개의 트윗을 위에서 살펴본 Key-value 자료구조로 관리해야합니다. 이 때 우리는 ‘좋아요가 높고’와 ‘최근에’ 사이에 존재하는 트레이드오프에 대해서 의사결정을 해야합니다. 

그에 앞서 위 요구사항이 나온 배경이자 근거가 되는 Jwitter의 mission statement는 다음과 같습니다.

1. *누구나 장벽 없이 실시간으로 아이디어와 정보를 생성하고 공유할 수 있도록 합니다.*
2. *누구나 장벽 없이 실시간으로 흥미롭고 관련성 높은 아이디어와 정보를 탐색할 수 있도록 합니다.*

홈 타임라인은 위 2번 미션을 수행하는 서비스라고 볼 수 있습니다. 따라서 흥미롭고 관련성 높은 실시간 트윗을 사용자가 탐색할 수 있게 제공하는 것을 목표로 하고 있는 것입니다. 그래서 Jwitter은 한정된 메모리의 제약사항 내에서 이 두가지 요구사항을 만족하기 위한 스코어 함수를 다음과 같이 디자인합니다.

> ***Design goal***
> 
> 
> ---
> 
> 좋아요에 적정한 가중치를 부여하여 다음과 같은 양 극단적 상황을 방지한다. 
> 
> 1. 최신 트윗들로만 상단에 노출되면 다른 좋아요를 많이 받은 지난 트윗들이 노출될 기회를 얻지 못함
> 2. 좋아요를 많이 받은 순으로 노출되면 당연히 좋아요가 없는 최신 트윗들은 노출될 기회가 없음

$$
\begin{align}Score(\#likes, time) &= time(Unix) + w*\#likes\\w &:= \frac{86,400}{v}\\v&:= \#likes\;required\;to\; last\;a\;full\;day\end{align}
$$

이렇게 디자인된 스코어 값은 [홈 타임라인](https://www.notion.so/Jwitter-e1968f7cf2f24369a9832103a7e44816?pvs=21) zset의 내림차순 정렬 기준이 됩니다.

<aside>
<img src="https://www.notion.so/icons/bookmark_gray.svg" alt="https://www.notion.so/icons/bookmark_gray.svg" width="40px" /> **예시**: 현재 시각에 게시된 트윗과 다음 날 똑같은 시각에 게시된 트윗과의 차이를 초로 계산했을 때 86,400이 나옵니다. 이 때 만약 변수 $v$를 200으로 설정했다면 그 의미는 200개의 좋아요를 받았을 때 $w$값을 통해 위 두 트윗의 스코어를 같게 만들어주게 됩니다. 따라서 $v$에 어떤 값을 부여하느냐에 따라 하루를 버틸 수 있게 만들어주는 좋아요의 개수가 달라지게 됩니다.

</aside>

### Wrap-up

<aside>
<img src="https://www.notion.so/icons/book_gray.svg" alt="https://www.notion.so/icons/book_gray.svg" width="40px" /> **Further work**
1. Premature replacement
2. Beginner’s unluck
3. Ghost tweet

</aside>

**Premature replacement**

위 스코어 함수의 흥미롭고 관련성 높은 실시간 트윗에 높은 점수를 주는 순기능이 제대로 작동하기 위해서는 애초에 다른 사람들의 평가(좋아요)를 받을 수 있는 충분한 시간이 주어져야 한다는 것입니다.

![자라지도 않은 나무를…😢](Jwitter%20e1968f7cf2f24369a9832103a7e44816/Untitled%205.png)

자라지도 않은 나무를…😢

만약 어떤 사용자가 5,000명의 팔로워를 가지고 있고(*실제 트위터의 Follow 제한 숫자입니다) 특정 시간대에 팔로워들의 트윗들이 몰려서 게시되는 상황이라고 해보겠습니다. 그렇게 되면 800개로 트윗이 제한된 상황에서  어떤 트윗들은 위 스코어 함수에 따라 바로 다른 트윗에 교체당하게 될 것입니다. 이렇게 퇴출당한 트윗이 만약 흥미롭고 관련성 높은 트윗이었다면 위 목표 달성에 실패한 것이 됩니다.
