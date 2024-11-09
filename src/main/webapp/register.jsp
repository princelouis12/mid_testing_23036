<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.auca.librarymanagement.model.*" %>
<%@ page import="com.auca.librarymanagement.dao.*" %>
<%@ page import="java.util.*" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Register - Library Management System</title>
    <link href="https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css" rel="stylesheet">
    <link href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
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
        }

        .registration-container {
            max-width: 800px;
            margin: 2rem auto;
            padding: 2rem;
            background: rgba(255, 255, 255, 0.95);
            border-radius: 20px;
            box-shadow: 0 8px 32px rgba(31, 38, 135, 0.15);
            backdrop-filter: blur(4px);
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

        .creative-input:focus ~ .input-icon,
        .creative-input:not(:placeholder-shown) ~ .input-icon {
            color: var(--primary-color);
            transform: translateY(-50%) scale(1.1);
        }

        .input-icon {
            position: absolute;
            right: 1rem;
            top: 50%;
            transform: translateY(-50%);
            color: #666;
            transition: all 0.3s ease;
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
            background: linear-gradient(
                120deg,
                transparent,
                rgba(255, 255, 255, 0.3),
                transparent
            );
            transition: 0.5s;
        }

        .submit-btn:hover:before {
            left: 100%;
        }

        .form-row {
            margin-left: -8px;
            margin-right: -8px;
        }

        .form-col {
            padding: 0 8px;
        }

        /* Creative progress bar for password strength */
        .password-strength {
            height: 4px;
            background: #e1e1e1;
            border-radius: 2px;
            margin-top: 0.5rem;
            overflow: hidden;
            position: relative;
        }

        .password-strength-bar {
            height: 100%;
            width: 0;
            background: linear-gradient(90deg, #ff6b6b, #4a90e2);
            transition: width 0.3s ease;
        }

        /* Floating animation for icons */
        @keyframes float {
            0% { transform: translateY(-50%) translateX(0); }
            50% { transform: translateY(-50%) translateX(5px); }
            100% { transform: translateY(-50%) translateX(0); }
        }

        .input-icon {
            animation: float 3s ease-in-out infinite;
        }

        /* Footer styling */
        footer {
            background: rgba(255, 255, 255, 0.9);
            backdrop-filter: blur(10px);
            border-top: 1px solid rgba(255, 255, 255, 0.5);
        }

        .footer-links a {
            color: var(--text-color);
            transition: color 0.3s ease;
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
        <div class="registration-container">
            <h2 class="text-center mb-5 font-weight-bold text-primary">
                <i class="fas fa-user-plus mr-2"></i>Join Our Library Community
            </h2>

            <!-- Server-side errors -->
            <% if (request.getAttribute("error") != null) { %>
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                    <i class="fas fa-exclamation-circle mr-2"></i>
                    <%= request.getAttribute("error") %>
                    <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
            <% } %>

            <form id="registrationForm" action="register" method="post" onsubmit="return validateForm()">
                <!-- Name Row -->
                <div class="form-row mb-4">
                    <div class="col-md-6 form-col">
                        <div class="form-group">
                            <input type="text" class="creative-input" id="firstName" name="firstName" placeholder=" " required>
                            <label for="firstName" class="creative-label">First Name</label>
                            <i class="fas fa-user input-icon"></i>
                        </div>
                    </div>
                    <div class="col-md-6 form-col">
                        <div class="form-group">
                            <input type="text" class="creative-input" id="lastName" name="lastName" placeholder=" " required>
                            <label for="lastName" class="creative-label">Last Name</label>
                            <i class="fas fa-user input-icon"></i>
                        </div>
                    </div>
                </div>

                <!-- Username -->
                <div class="form-group">
                    <input type="text" class="creative-input" id="userName" name="userName" placeholder=" " required>
                    <label for="userName" class="creative-label">Username</label>
                    <i class="fas fa-at input-icon"></i>
                </div>

                <!-- Password -->
                <div class="form-group">
                    <input type="password" class="creative-input" id="password" name="password" placeholder=" " required minlength="8">
                    <label for="password" class="creative-label">Password</label>
                    <i class="fas fa-lock input-icon"></i>
                    <div class="password-strength">
                        <div class="password-strength-bar"></div>
                    </div>
                </div>

                <!-- Phone Number -->
                <div class="form-group">
                    <input type="tel" class="creative-input" id="phoneNumber" name="phoneNumber" placeholder=" " required pattern="[0-9]{10}">
                    <label for="phoneNumber" class="creative-label">Phone Number</label>
                    <i class="fas fa-phone input-icon"></i>
                </div>

                <!-- Gender and Role Row -->
                <div class="form-row mb-4">
                    <div class="col-md-6 form-col">
                        <div class="form-group">
                            <select class="creative-input" id="gender" name="gender" required>
                                <option value="" disabled selected></option>
                                <option value="MALE">Male</option>
                                <option value="FEMALE">Female</option>
                            </select>
                            <label for="gender" class="creative-label">Gender</label>
                            <i class="fas fa-venus-mars input-icon"></i>
                        </div>
                    </div>
                    <div class="col-md-6 form-col">
                        <div class="form-group">
                            <select class="creative-input" id="role" name="role" required>
                                <option value="" disabled selected></option>
                                <option value="STUDENT">Student</option>
                                <option value="TEACHER">Teacher</option>
                                <option value="MANAGER">Manager</option>
                                <option value="DEAN">Dean Of Study</option>
                                <option value="HOD">Head Of Department</option>
                                <option value="LIBRARIAN">Librarian</option>
                            </select>
                            <label for="role" class="creative-label">Role</label>
                            <i class="fas fa-user-tag input-icon"></i>
                        </div>
                    </div>
                </div>

                <!-- Village -->
                <div class="form-group">
                    <select class="creative-input" id="villageId" name="villageId" required>
                        <option value="" disabled selected></option>
                        <% try { 
                            UserDao userDao = new UserDao(); 
                            List<Location> villages = userDao.findAllVillages(); 
                            for (Location village : villages) { %>
                                <option value="<%= village.getLocationId() %>"><%= village.getLocationName() %></option>
                        <% }} catch (Exception e) { 
                            e.printStackTrace(); 
                            request.setAttribute("error", "Error loading villages. Please try again later."); 
                        } %>
                    </select>
                    <label for="villageId" class="creative-label">Village</label>
                    <i class="fas fa-map-marker-alt input-icon"></i>
                </div>

                <!-- Submit Button -->
                <button type="submit" class="submit-btn">
                    <i class="fas fa-user-plus mr-2"></i>Create Account
                </button>

                <!-- Login Link -->
                <div class="text-center mt-4">
                    <p class="text-muted">
                        Already have an account? 
                        <a href="login.jsp" class="text-primary font-weight-bold">
                            <i class="fas fa-sign-in-alt mr-1"></i>Login
                        </a>
                    </p>
                </div>
            </form>
        </div>
    </div>

    <!-- Footer -->
    <footer class="py-4 mt-5">
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

    <!-- JavaScript for Validation and Language Switching -->
    <script src="https://code.jquery.com/jquery-3.5.1.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.5.4/dist/umd/popper.min.js"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
    <script>
        // Password strength indicator
        // Password strength indicator
document.getElementById('password').addEventListener('input', function(e) {
    const password = e.target.value;
    let strength = 0;
    
    if(password.match(/[a-z]/)) strength += 20;
    if(password.match(/[A-Z]/)) strength += 20;
    if(password.match(/[0-9]/)) strength += 20;
    if(password.match(/[^a-zA-Z0-9]/)) strength += 20;
    if(password.length >= 8) strength += 20;

    const strengthBar = document.querySelector('.password-strength-bar');
    strengthBar.style.width = strength + '%';
    
    // Change color based on strength
    if(strength < 40) {
        strengthBar.style.background = 'linear-gradient(90deg, #ff4646, #ff6b6b)';
    } else if(strength < 80) {
        strengthBar.style.background = 'linear-gradient(90deg, #ffa500, #ffd700)';
    } else {
        strengthBar.style.background = 'linear-gradient(90deg, #4CAF50, #45a049)';
    }
});

// Form validation
function validateForm() {
    const userName = document.getElementById("userName").value;
    const password = document.getElementById("password").value;
    const phoneNumber = document.getElementById("phoneNumber").value;
    let isValid = true;
    let errorMessages = [];

    // Reset any previous error states
    document.querySelectorAll('.creative-input').forEach(input => {
        input.classList.remove('is-invalid');
    });

    // Remove any existing error alerts
    const existingAlert = document.querySelector('.alert-danger');
    if (existingAlert) {
        existingAlert.remove();
    }

    // Username validation
    if (!userName.match(/^[a-zA-Z0-9_]{4,50}$/)) {
        document.getElementById("userName").classList.add('is-invalid');
        errorMessages.push("Username must be 4-50 characters long and contain only letters, numbers, and underscores");
        isValid = false;
    }

    // Password validation
    if (!password.match(/^(?=.*[A-Z])(?=.*\d)[A-Za-z\d@$!%*?&]{8,20}$/)) {
        document.getElementById("password").classList.add('is-invalid');
        errorMessages.push("Password must be 8-20 characters long with at least one uppercase letter and one number");
        isValid = false;
    }

    // Phone number validation
    if (!phoneNumber.match(/^\d{10}$/)) {
        document.getElementById("phoneNumber").classList.add('is-invalid');
        errorMessages.push("Phone number must be exactly 10 digits");
        isValid = false;
    }

    if (!isValid) {
        // Create error alert
        const errorDiv = document.createElement('div');
        errorDiv.className = 'alert alert-danger alert-dismissible fade show mt-3';
        errorDiv.role = 'alert';
        
        // Build error message HTML
        let errorHtml = '<strong><i class="fas fa-exclamation-circle"></i> Please correct the following:</strong><ul class="mb-0 mt-2">';
        errorMessages.forEach(function(error) {
            errorHtml += '<li>' + error + '</li>';
        });
        errorHtml += '</ul><button type="button" class="close" data-dismiss="alert" aria-label="Close">' +
                    '<span aria-hidden="true">&times;</span></button>';
        
        errorDiv.innerHTML = errorHtml;
        document.getElementById('registrationForm').prepend(errorDiv);
    }

    return isValid;
}

// Language switching functionality
function switchLanguage(lang) {
    const translations = {
        en: {
            title: 'Join Our Library Community',
            firstName: 'First Name',
            lastName: 'Last Name',
            userName: 'Username',
            password: 'Password',
            phoneNumber: 'Phone Number',
            gender: 'Gender',
            role: 'Role',
            village: 'Village',
            submit: 'Create Account',
            login: 'Already have an account? Login',
            male: 'Male',
            female: 'Female'
        },
        fr: {
            title: 'Rejoignez Notre Communauté',
            firstName: 'Prénom',
            lastName: 'Nom',
            userName: 'Nom d\'utilisateur',
            password: 'Mot de passe',
            phoneNumber: 'Numéro de téléphone',
            gender: 'Genre',
            role: 'Rôle',
            village: 'Village',
            submit: 'Créer un compte',
            login: 'Vous avez déjà un compte? Connectez-vous',
            male: 'Masculin',
            female: 'Féminin'
        }
    };

    const t = translations[lang];
    
    // Update all text content
    document.querySelector('h2').innerHTML = '<i class="fas fa-user-plus mr-2"></i>' + t.title;
    document.querySelectorAll('.creative-label').forEach(label => {
        const forAttr = label.getAttribute('for');
        if (t[forAttr]) {
            label.textContent = t[forAttr];
        }
    });

    // Update select options
    if (document.getElementById('gender')) {
        const genderSelect = document.getElementById('gender');
        const options = genderSelect.options;
        options[1].text = t.male;
        options[2].text = t.female;
    }

    // Update submit button
    document.querySelector('.submit-btn').innerHTML = '<i class="fas fa-user-plus mr-2"></i>' + t.submit;

    // Update login link
    document.querySelector('.text-center.mt-4 p').innerHTML = 
        t.login + ' <a href="login.jsp" class="text-primary font-weight-bold">' +
        '<i class="fas fa-sign-in-alt mr-1"></i></a>';
}

// Add smooth floating effect to icons on input focus
document.querySelectorAll('.creative-input').forEach(input => {
    input.addEventListener('focus', function() {
        const icon = this.parentElement.querySelector('.input-icon');
        icon.style.animation = 'none';
        icon.offsetHeight; // Trigger reflow
        icon.style.animation = 'float 3s ease-in-out infinite';
    });
});
    </script>
</body>
</html>