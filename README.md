*Some questions you might be asking yourself*

1. Why fields are not private in almost all classes?

Actually, they are! You can find lombok.config file in the root directory of the project, where thr defaultPrivate property is set to true.

2. Why MapStruct?

This task is actully pretty simple and ObjectMapper could be a sufficient choice for mapping DTO to entity and back. However, in real-world applications, there are much more domains and mappings could be more complex (even now in patch endpoint without MapStruct unset properties would have required manual population if ObjectMapper had been used). 
Thats why I decided that it would be nice to demonstrate my abilities to work with mapping libraries.

3. Why some errors are logged as warnings?

Some exceptions that during request processing are pretty common, and may even be named normal case (like EntityNotFoundException when invalid id is recieved). Logging this exceptions as errors would pollute logs that contain valuable information about cases when the application fails with some regular information, creating noise and reducing readability.
Thats why things like invalid format, entity not found or constraint violations are logged as warnings.

4. Why no caching?

Currently, endpoints do not contain any heavy computations nor it is expected to be a high-load system. Thats why caching here would be just redundant.

5. No security?

While security is often a non-functional requirment that is not directly mentioned in tech task, I decided to omit it here. Things like JWT auth require much effort and make testing and demonstration more complex, so I decided to drop this idea.

6. Swagger

For more comfortable demo, you could go to /swagger-ui/index.html. There you could find example schemas and descriptions of response codes, as well as example request results.

7. In our requirments entity field "Birth date" must be before today, while you have it annotated with @AdultBirthday. Why?

Well, I figured that if a user could be created only if they are an adult, then entity should have this constraint too. This helps to avoid possible data inconcistencies sinse @AdultBirthday is more strict constraint than "before now".
