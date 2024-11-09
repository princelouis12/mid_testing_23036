<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - Library Management System</title>
    <link href="https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css" rel="stylesheet">
    <link href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
    <style>
        :root {
            --primary-color: #4a90e2;
            --secondary-color: #f8f9fa;
            --accent-color: #ff6b6b;
            --text-color: #2c3e50;
        }

        body {
            background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
            min-height: 100vh;
            display: flex;
            flex-direction: column;
        }

        .login-container {
            max-width: 500px;
            margin: 2rem auto;
            padding: 2rem;
            background: rgba(255, 255, 255, 0.95);
            border-radius: 20px;
            box-shadow: 0 8px 32px rgba(31, 38, 135, 0.15);
            backdrop-filter: blur(4px);
            animation: fadeIn 0.5s ease-out;
        }

        @keyframes fadeIn {
            from { opacity: 0; transform: translateY(-20px); }
            to { opacity: 1; transform: translateY(0); }
        }

        .form-group {
            position: relative;
            margin-bottom: 1.5rem;
        }

        .creative-input {
            width: 100%;
            padding: 1.2rem 1rem 0.5rem;
            border: 2px solid #e1e1e1;
            border-radius: 12px;
            font-size: 1rem;
            transition: all 0.3s ease;
            background: transparent;
            color: var(--text-color);
        }

        .creative-label {
            position: absolute;
            left: 1rem;
            top: 1rem;
            padding: 0 0.5rem;
            color: #666;
            font-size: 1rem;
            transition: all 0.3s ease;
            pointer-events: none;
            background: transparent;
        }

        .creative-input:focus,
        .creative-input:not(:placeholder-shown) {
            border-color: var(--primary-color);
            box-shadow: 0 0 0 4px rgba(74, 144, 226, 0.1);
        }

        .creative-input:focus ~ .creative-label,
        .creative-input:not(:placeholder-shown) ~ .creative-label {
            transform: translateY(-1.4rem) translateX(-0.5rem) scale(0.8);
            color: var(--primary-color);
            font-weight: 600;
            background: white;
            padding: 0 0.5rem;
            border-radius: 4px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }

        .input-icon {
            position: absolute;
            right: 1rem;
            top: 50%;
            transform: translateY(-50%);
            color: #666;
            transition: all 0.3s ease;
            animation: float 3s ease-in-out infinite;
        }

        @keyframes float {
            0% { transform: translateY(-50%) translateX(0); }
            50% { transform: translateY(-50%) translateX(5px); }
            100% { transform: translateY(-50%) translateX(0); }
        }

        .submit-btn {
            background: linear-gradient(45deg, var(--primary-color), #6ab1f7);
            color: white;
            border: none;
            padding: 1rem 2rem;
            border-radius: 12px;
            font-weight: 600;
            letter-spacing: 0.5px;
            transition: all 0.3s ease;
            width: 100%;
            position: relative;
            overflow: hidden;
        }

        .submit-btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(74, 144, 226, 0.3);
        }

        .submit-btn:before {
            content: '';
            position: absolute;
            top: 0;
            left: -100%;
            width: 100%;
            height: 100%;
            background: linear-gradient(120deg, transparent, rgba(255, 255, 255, 0.3), transparent);
            transition: 0.5s;
        }

        .submit-btn:hover:before {
            left: 100%;
        }

        .register-link {
            text-align: center;
            margin-top: 1.5rem;
            font-size: 0.95rem;
            color: var(--text-color);
            transition: all 0.3s ease;
        }

        .register-link a {
            color: var(--primary-color);
            font-weight: 600;
            text-decoration: none;
            margin-left: 0.5rem;
            transition: all 0.3s ease;
        }

        .register-link a:hover {
            color: #2c75c5;
            text-decoration: underline;
        }
        
        footer {
            background: rgba(255, 255, 255, 0.9);
            backdrop-filter: blur(10px);
            border-top: 1px solid rgba(255, 255, 255, 0.5);
            margin-top: auto;
            padding: 1rem 0;
        }

        .footer-links {
            display: flex;
            justify-content: center;
            align-items: center;
            gap: 1rem;
        }

        .footer-links a {
            color: var(--text-color);
            transition: color 0.3s ease;
            text-decoration: none;
        }

        .footer-links a:hover {
            color: var(--primary-color);
        }

        /* Language selector styling */
        .language-dropdown select {
            background: rgba(255, 255, 255, 0.9);
            border: 2px solid var(--primary-color);
            border-radius: 20px;
            padding: 0.5rem 2rem;
            color: var(--text-color);
            cursor: pointer;
            transition: all 0.3s ease;
        }

        .language-dropdown select:hover {
            background: var(--primary-color);
            color: white;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="login-container">
            <h2 class="text-center mb-5 font-weight-bold text-primary">
                <i class="fas fa-book-reader mr-2"></i>Library Login
            </h2>

            <form id="loginForm" class="space-y-4">
                <!-- Username -->
                <div class="form-group">
                    <input type="text" class="creative-input" id="userName" name="userName" placeholder=" " required>
                    <label for="userName" class="creative-label">Username</label>
                    <i class="fas fa-user input-icon"></i>
                </div>
                
                <!-- Password -->
                <div class="form-group">
                    <input type="password" class="creative-input" id="password" name="password" placeholder=" " required>
                    <label for="password" class="creative-label">Password</label>
                    <i class="fas fa-lock input-icon"></i>
                </div>
                
                <!-- Submit Button -->
                <button type="submit" class="submit-btn">
                    <i class="fas fa-sign-in-alt mr-2"></i>Login
                </button>

                <!-- Register Link -->
                <div class="register-link">
                    <span>Don't have an account?</span>
                    <a href="register.jsp">
                        <i class="fas fa-user-plus mr-1"></i>Register here
                    </a>
                </div>
            </form>
        </div>
    </div>
    
    <!-- Footer -->
    <footer class="py-4">
        <div class="container">
            <div class="row align-items-center justify-content-between">
                <div class="col-auto">
                    <div class="language-dropdown">
                        <select id="language" onchange="switchLanguage(this.value)">
                            <option value="" disabled selected>Language</option>
                            <option value="en">English</option>
                            <option value="fr">Français</option>
                        </select>
                    </div>
                </div>
                <div class="col">
                    <div class="footer-links text-center">
                        <span>© 2024</span>
                        <a href="#" class="ml-3">About</a>
                        <a href="#" class="ml-3">Privacy Policy</a>
                        <a href="#" class="ml-3">Terms</a>
                    </div>
                </div>
            </div>
        </div>
    </footer>

    <!-- Scripts -->
    <script src="https://code.jquery.com/jquery-3.5.1.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.5.4/dist/umd/popper.min.js"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
    <script>
        // Add floating effect to icons
        document.querySelectorAll('.creative-input').forEach(input => {
            input.addEventListener('focus', function() {
                const icon = this.parentElement.querySelector('.input-icon');
                icon.style.animation = 'none';
                icon.offsetHeight; // Trigger reflow
                icon.style.animation = 'float 3s ease-in-out infinite';
            });
        });
        
     // Language switching functionality
        function switchLanguage(lang) {
            const translations = {
                en: {
                    title: 'Library Login',
                    userName: 'Username',
                    password: 'Password',
                    submit: 'Login',
                    register: 'Don\'t have an account?',
                    registerLink: 'Register here'
                },
                fr: {
                    title: 'Connexion à la Bibliothèque',
                    userName: 'Nom d\'utilisateur',
                    password: 'Mot de passe',
                    submit: 'Se connecter',
                    register: 'Vous n\'avez pas de compte?',
                    registerLink: 'Inscrivez-vous ici'
                }
            };

            const t = translations[lang];
            
            // Update all text content
            document.querySelector('h2').innerHTML = '<i class="fas fa-book-reader mr-2"></i>' + t.title;
            
            // Update labels
            document.querySelectorAll('.creative-label').forEach(label => {
                const forAttr = label.getAttribute('for');
                if (t[forAttr]) {
                    label.textContent = t[forAttr];
                }
            });

            // Update submit button
            document.querySelector('.submit-btn').innerHTML = 
                '<i class="fas fa-sign-in-alt mr-2"></i>' + t.submit;

            // Update register link
            const registerDiv = document.querySelector('.register-link');
            registerDiv.innerHTML = 
                '<span>' + t.register + '</span> ' +
                '<a href="register.jsp">' +
                '<i class="fas fa-user-plus mr-1"></i>' + t.registerLink +
                '</a>';
        }

        // Form submission handler
        document.getElementById('loginForm').addEventListener('submit', function(e) {
            e.preventDefault();
            
            const formData = new FormData(this);
            
            fetch('login', {
                method: 'POST',
                body: new URLSearchParams(formData),
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                }
            })
            .then(response => response.json())
            .then(data => {
                if (data.success === "true") {
                    Swal.fire({
                        icon: 'success',
                        title: 'Welcome Back!',
                        text: 'Login successful',
                        timer: 1500,
                        showConfirmButton: false,
                        customClass: {
                            popup: 'animated fadeInDown'
                        }
                    }).then(() => {
                        window.location.href = data.redirect;
                    });
                } else {
                    Swal.fire({
                        icon: 'error',
                        title: 'Login Failed',
                        text: data.error || 'Invalid username or password',
                        customClass: {
                            popup: 'animated shake'
                        }
                    });
                }
            })
            .catch(error => {
                Swal.fire({
                    icon: 'error',
                    title: 'Error',
                    text: 'An error occurred during login. Please try again.',
                    customClass: {
                        popup: 'animated fadeIn'
                    }
                });
                console.error('Error:', error);
            });
        });
    </script>
</body>
</html>