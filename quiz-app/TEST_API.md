# Quick Test - Register and Login

## Register a User
```powershell
# Register an admin user
Invoke-RestMethod -Uri "http://localhost:9090/api/auth/register" -Method POST -ContentType "application/json" -Body '{"username":"admin","password":"admin123","role":"ADMIN"}'

# Register a student user
Invoke-RestMethod -Uri "http://localhost:9090/api/auth/register" -Method POST -ContentType "application/json" -Body '{"username":"student","password":"student123","role":"STUDENT"}'
```

## Login
```powershell
# Login as admin
Invoke-RestMethod -Uri "http://localhost:9090/api/auth/login" -Method POST -ContentType "application/json" -Body '{"username":"admin","password":"admin123"}'

# Login as student  
Invoke-RestMethod -Uri "http://localhost:9090/api/auth/login" -Method POST -ContentType "application/json" -Body '{"username":"student","password":"student123"}'
```
