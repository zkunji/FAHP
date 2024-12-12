## FAHP项目后端开发注意事项

### 一、关于代码提交

这次的代码不要直接提交推送到主仓库，按照以下步骤进行操作

1. 首先在自己的`GitHub`账户下`fork FAHP`这个仓库.
2. 将`fork`的仓库克隆到本地环境.
3. 在本地创建一个新分支进行开发和提交修改.
4. 将本地分支的更改推送到`GitHub`上的`fork`仓库.
5. 在`GitHub`上从自己的`fork`仓库的分支向原仓库发起一个`Pull Request`.

<h3>二、关于代码风格</h3>

1. **类名与接口名**：采用大驼峰命名法，例如

   ```java
   //类
   public class UserService {
       //...
   }
   
   //接口
   public interface UserMapper extends BaseMapper<User> {
       //...
   }
   ```

2. **变量名**：采用小驼峰命名法，例如

   ```java
   private UserService userService;
   private UserMapper userMapper;
   ...
   ```

3. **URL路径命名**：

   - 使用小写字母.
   - 尽量用单个词汇.
   - 单词之间用 "-"作为连字符.
   - 不要在路径中使用空格.
   - 表示集合的资源使用复数形式.

   ```java
   /users：获取用户列表
   
   /users/{userId}：通过ID获取单个用户。
   
   /payments/process：处理支付请求。
   
   @RestController
   @RequestMapping("/users")
   public class UserController {
   
       @GetMapping
       public ResponseEntity<List<User>> getAllUsers() {
           // ...
       }
   
       @GetMapping("/{userId}")
       public ResponseEntity<User> getUserById(@PathVariable Long userId) {
           // ...
       }
   
       @PostMapping
       public ResponseEntity<User> createUser(@RequestBody User user) {
           // ...
       }
   }
   ```

4. **方法命名**：

   - 采用小驼峰命名.
   - 不要过于复杂，尽量简洁易懂.
   - 在每个方法前应有注释，表明方法的作用.
   - 函数返回值统一为`Result`，非必要情况禁止使用其他的返回类型.
   - 关于重写方法，请统一使用`Ctrl+O`快捷键进行重写，如果要自己手动重写，请在重写的方法上加上`@Override`注解.

   ```java
   UserService,java
       
   public interface UserService extends IService<User> {
       
       Result register(Map<String, String> params);
   
       Result login(Map<String, String> params);
       
       Result updateUserinfo(Map<String, String> params, long uid);
       
       ...
   }
   
   
   UserServiceImpl,java
       
   public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
       
       @Override
       public Result login(Map<String, String> params) {
           //...
       }
       
       //...
           
   }
   ```

5. **代码拆分**：若代码过于复杂时，可以将不属于业务逻辑的功能性代码拆分，放置`util`包内



