# 05. 스프링 MVC - 구조 이해

# 스프링 MVC 전체 구조

![제목](../이미지/05_스프링_MVC_구조_이해.png)

### 직접 만든 프레임워크  스프링 MVC 비교

- FrontController  DispatcherServlet
- handlerMappingMap  HandlerMapping
- MyHandlerAdapter  HandlerAdapter
- ModelView  ModelAndView
- viewResolver  ViewResolver
- MyView  View

## DispatcherServlet

스프링 MVC도 프론트 컨트롤러 패턴으로 구현되어 있다.
스프링 MVC의 프론트 컨트롤러가 바로 디스패처 서블릿(DispatcherServlet)이다.
그리고 이 디스패처 서블릿이 바로 스프링 MVC의 핵심이다.

**DispacherServlet 서블릿 등록**

- DispacherServlet도 부모 클래스에서 HttpServlet을 상속 받아서 사용하고, 서블릿으로 동작한다.
DispatcherServlet →  FrameworkServlet →  HttpServletBean →  HttpServlet
- 스프링 부트는 DispacherServlet을 서블릿으로 자동으로 등록하면서 모든 경로(urlPatterns="/")에
대해서 매핑한다.
    - 참고: 더 자세한 경로가 우선순위가 높다. 그래서 기존에 등록한 서블릿도 함께 동작한다.

**요청 흐름**

- 서블릿이 호출되면 HttpServlet이 제공하는 serivce()가 호출된다.
- 스프링 MVC는 DispatcherServlet의 부모인 FrameworkServlet에서 service()를 오버라이드
해두었다.
- FrameworkServlet.service()를 시작으로 여러 메서드가 호출되면서
DispacherServlet.doDispatch() 가 호출된다.

**DispacherServlet.doDispatch()**

1. **핸들러 조회**: 핸들러 매핑을 통해 요청 URL에 매핑된 핸들러(컨트롤러)를 조회한다.
2. **핸들러 어댑터 조회**: 핸들러를 실행할 수 있는 핸들러 어댑터를 조회한다.
3. **핸들러 어댑터 실행**: 핸들러 어댑터를 실행한다.
4. **핸들러 실행**: 핸들러 어댑터가 실제 핸들러를 실행한다.
5. **ModelAndView 반환**: 핸들러 어댑터는 핸들러가 반환하는 정보를 ModelAndView로 변환해서
반환한다.
6. **viewResolver 호출**: 뷰 리졸버를 찾고 실행한다.
JSP의 경우: InternalResourceViewResolver가 자동 등록되고, 사용된다.
7. **View 반환**: 뷰 리졸버는 뷰의 논리 이름을 물리 이름으로 바꾸고, 렌더링 역할을 담당하는 뷰 객체를
반환한다.
JSP의 경우 InternalResourceView(JstlView)를 반환하는데, 내부에 forward() 로직이 있다.
8. **뷰 렌더링**: 뷰를 통해서 뷰를 렌더링 한다.

### 주요 인터페이스 목록

- 핸들러 매핑: `org.springframework.web.servlet.HandlerMapping`
- 핸들러 어댑터: `org.springframework.web.servlet.HandlerAdapter`
- 뷰 리졸버: `org.springframework.web.servlet.ViewResolver`
- 뷰: `org.springframework.web.servlet.View`

→  스프링 MVC의 큰 강점은 `DispatcherServlet` 코드의 변경 없이, 원하는 기능을 변경하거나 확장할 수
있다는 점이다. 지금까지 설명한 대부분을 확장 가능할 수 있게 `인터페이스로 제공`

# 핸들러 매핑과 핸들러 어댑터

## 과거

Controller 인터페이스
과거 버전 스프링 컨트롤러

```java
@Component("/springmvc/old-controller")
public class OldController implements Controller {
    @Override
    public ModelAndView handleRequest(HttpServletRequest request,
HttpServletResponse response) throws Exception {
        System.out.println("OldController.handleRequest");
        return null;
    }
}
```

- @Component: 이 컨트롤러는 /springmvc/old-controller라는 이름의 스프링 빈으로 등록되었다.
- 빈의 이름으로 URL을 매핑할 것이다.

### HandlerMapping; 핸들러 매핑

- Handler Mapping에서 특정 Controller를 찾을 수 있어야 함
- ex: Spring bean의 이름으로 Handler를 찾을 수 있는 Handler Mapping 필요

```
0 = RequestMappingHandlerMapping// 애노테이션 기반의 컨트롤러인 @RequestMapping에서 사용
1 = BeanNameUrlHandlerMapping// 스프링 빈의 이름으로 핸들러를 찾기
```

### HandlerAdapter; 핸들러 어댑터

- Handler Mapping을 통해 찾은 Handler를 실행할 수 있는 Handler Adapter가 필요
- ex: Controller 인터페이스를 실행할 수 있는 핸들러 어댑터를 찾고 실행해야 함

```
0 = RequestMappingHandlerAdapter// 애노테이션 기반의 컨트롤러인 @RequestMapping에서 사용
1 = HttpRequestHandlerAdapter// HttpRequestHandler 처리
2 = SimpleControllerHandlerAdapter// Controller 인터페이스 처리
```

**핸들러 매핑으로 핸들러 조회**

1. HandlerMapping을 순서대로 실행해서, 핸들러를 찾는다.
2. 이 경우 빈 이름으로 핸들러를 찾아야 하기 때문에 이름 그대로 빈 이름으로 핸들러를 찾아주는
`BeanNameUrlHandlerMapping` 가 실행에 성공하고 핸들러인 OldController를 반환한다.

**핸들러 어댑터 조회**

1. HandlerAdapter의 supports()를 순서대로 호출한다.
2. SimpleControllerHandlerAdapter가 Controller 인터페이스를 지원하므로 대상이 된다.

**핸들러 어댑터 실행**

1. 디스패처 서블릿이 조회한 `SimpleControllerHandlerAdapter`를 실행하면서 핸들러 정보도 함께
넘겨준다.
2. `SimpleControllerHandlerAdapter`는 핸들러인 OldController를 내부에서 실행하고, 그 결과를
반환한다.

### HttpRequestHandlerAdapter

```java
@Component("/springmvc/request-handler")
public class MyHttpRequestHandler implements HttpRequestHandler {
@Override
public void handleRequest(HttpServletRequest request, HttpServletResponse
response) throws ServletException, IOException {
System.out.println("MyHttpRequestHandler.handleRequest");
}
}
```

`BeanNameUrlHandlerMapping` →  `HttpRequestHandlerAdapter` → `MyHttpRequestHandler`

`HandlerMapping = BeanNameUrlHandlerMapping`
`HandlerAdapter = HttpRequestHandlerAdapter`

### RequestMapping

- RequestMappingHandlerMapping, RequestMappingHandlerAdapter 가장 우선순위가 높은 핸들러 매핑과 핸들러 어댑터
- @RequestMapping 의 앞글자를 따서 만든 이름인데, 이것이 바로 지금 스프링에서 주로 사용하는
애노테이션 기반의 컨트롤러를 지원하는 매핑과 어댑터
- 대부분 이 방식

# 뷰 리졸버

View를 사용할 수 있도록 다음 코드를 추가했다.
`return new ModelAndView("new-form");`

→ Whitelabel Error Page 오류

### 뷰 리졸버 - InternalResourceViewResolver

```java
spring.mvc.view.prefix=/WEB-INF/views/
spring.mvc.view.suffix=.jsp
```

- 스프링 부트는 InternalResourceViewResolver라는 뷰 리졸버를 자동으로 등록하는데, 이때
application.properties 에 등록한 spring.mvc.view.prefix, spring.mvc.view.suffix 설정
정보를 사용해서 등록한다.

→ 정상 작동

```java
1 = BeanNameViewResolver         : 빈 이름으로 뷰를 찾아서 반환한다. (예: 엑셀 파일 생성
기능에 사용)
2 = InternalResourceViewResolver : JSP를 처리할 수 있는 뷰를 반환한다.
```

### 동작 과정

1. 핸들러 어댑터 호출 // "new-form"이라는 논리 뷰 이름 획득
2. **ViewResolver** 호출 // BeanNameViewResolver는 new-form이라는 이름의 스프링 빈으로 등록된 뷰를 찾아야 하는데 없다 → InternalResourceViewResolver 호출
3. InternalResourceViewResolver // return InternalResourceView
4. InternalResourceView // JSP처럼 forward()를 호출해 처리할 수 있는 경우에 사용
5. view.render() // view.render() 호출 -> InternalResourceView는 forward() 사용해 JSP 실행

### @Controller

- 내부에 @Component 어노테이션이 존재해 컴포넌트 스캔의 대상
- 스프링이 자동으로 스프링 빈으로 등록
- 스프링 MVC에서 어노테이션 기반 컨트롤러로 인식하게 함

### @RequestMapping

- 요청 정보를 매핑
- 해당 url이 호출되면 해당 메소드가 호출
- 어노테이션 기반 동작이기 때문에 메소드 이름은 자유

+ 핸들러 매핑과 핸들러 어댑터 중 가장 우선순위가 높은 것은 RequestMappingHandlerMapping, RequestMappingHandlerAdapter이다. @RequestMapping의 앞글자를 따서 만들어졌다.

### SpringMemberFormControllerV1

```
@Controller
public class SpringMemberFormControllerV1 {
    @RequestMapping("/springmvc/v1/members/new-form")
    private ModelAndView process() {
        return new ModelAndView("new-form");
    }
}

```

- ModelAndView: 모델, 뷰 정보 담아서 반환
- @Controller : 스프링이 자동으로 스프링 빈으로 등록한다. (내부에 @Component 애노테이션이 있어서 컴포넌트 스캔의 대상이 됨)
- @RequestMapping : 요청 정보를 매핑한다. 해당 URL이 호출되면 이 메서드가 호출된다.

### SpringMemberSaveControllerV1

```
@Controller
public class SpringMemberSaveControllerV1 {
    private MemberRepository memberRepository = MemberRepository.getInstance();

    @RequestMapping("/springmvc/v1/members/save")
    private ModelAndView process(HttpServletRequest req, HttpServletResponse res) {
        String username = req.getParameter("username");
        int age = Integer.parseInt(req.getParameter("age"));

        Member member = new Member(username, age);
        memberRepository.save(member);

        ModelAndView mv = new ModelAndView("save-result");
        mv.addObject("member", member);
        return mv;
    }
}
```

- addObject(): 스프링이 제공하는 ModelAndView를 통해 Model 데이터 추가시 해당 메소드 사용
- mv는 이후 뷰를 렌더링 할 때 사용

### SpringMemberListControllerV1

```
@Controller
public class SpringMemberListControllerV1 {
    MemberRepository memberRepository = MemberRepository.getInstance();

    @RequestMapping("/springmvc/v1/members")
    public ModelAndView process() {
        List<Member> members = memberRepository.findAll();

        ModelAndView mv = new ModelAndView("members");
        mv.getModel().put("members", members);

        return mv;
    }
}
```

동작 과정에 대해서는 이전 게시글에서 자세히 설명한게 많으니 생략한다.

코드가 훨씬 간편해진 것이 보이지만 @RequestMapping이 클래스가 아닌 메소드 단위에 적용되어 있다.

컨트롤러 클래스를 좀 더 유연하게 통합해보자.

### SpringMemberControllerV2

```
@Controller
@RequestMapping("/springmvc/v2/members")
public class SpringMemberControllerV2 {
    MemberRepository memberRepository = MemberRepository.getInstance();

    @RequestMapping("/new-form")
    private ModelAndView newForm() {
        return new ModelAndView("new-form");
    }

    @RequestMapping
    public ModelAndView members() {
        List<Member> members = memberRepository.findAll();

        ModelAndView mv = new ModelAndView("members");
        mv.getModel().put("members", members);

        return mv;
    }

    @RequestMapping("/save")
    private ModelAndView save(HttpServletRequest req, HttpServletResponse res) {
        String username = req.getParameter("username");
        int age = Integer.parseInt(req.getParameter("age"));

        Member member = new Member(username, age);
        memberRepository.save(member);

        ModelAndView mv = new ModelAndView("save-result");
        mv.addObject("member", member);
        return mv;
    }
}
```

- 클래스 3개로 분리되었던 작업이 단 하나의 파일로 줄어들었다.
- RequestMapping이 클래스에도 붙고 메소드에도 붙는다.
- 위와 같은 경우에 save()를 부르려면 /spingmvc/v2/members/save로 접근하면 된다.

아직 좀 불편한 점이 남아있다.

ModelAndView를 매번 생성하고 반환해주는게 좀 번거롭게 느껴진다. mv.addObject("member", member)

실무에서 사용하는 것과 가깝게 더 개선해보자.

### SpringMemberControllerV3

```
@Controller
@RequestMapping("/springmvc/v3/members")
public class SpringMemberControllerV3 {
    private MemberRepository memberRepository = MemberRepository.getInstance();

    @RequestMapping("/new-form")
    private String newForm() {
        return "new-form";
    }

    @RequestMapping
    public String members(Model model) {
        List<Member> members = memberRepository.findAll();
        model.addAttribute("members",members);
        return "members";
    }

    @RequestMapping("/save")
    private String save(@RequestParam("username") String username, @RequestParam("age") int age, Model model) {
        Member member = new Member(username, age);
        memberRepository.save(member);

        model.addAttribute("member",member);
        return "save-result";
    }
}
```

- save(), members() 를 보면 Model을 파라미터로 받는 것을 확인할 수 있다. 스프링 MVC도 이런 편의
기능을 제공한다.
- 뷰 이름 직접 반환 : ModelAndView를 매번 생성하지 않아도 됨.
- @RequestParam: HttpServletRequest가 아니라 파라미터를 직접 받아올 수 있음-
    - @RequestParam("username") == request.getParameter("username")

위 코드는 POST방식 GET 방식 구분 없이 모두 적용되고 있다.

만약 POST 방식만 사용하고 싶다면 @RequestMapping을 다음과 같이 수정하면 된다.

```
@RequestMapping(value = "/save", method = RequestMethod.POST)
```

하지만 개발자들은 여기서 더 간편하게 개발함

```
@PostMapping("/save")
```