<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="contextPath" content="${pageContext.request.contextPath}">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.0/css/bootstrap.min.css" rel="stylesheet">
    <title>Location Management</title>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.0/css/bootstrap.min.css" rel="stylesheet">
    <style>
        .location-form {
            max-width: 800px;
            margin: 20px auto;
            padding: 20px;
            box-shadow: 0 0 10px rgba(0,0,0,0.1);
            border-radius: 8px;
        }
        .hierarchy-select {
            margin-bottom: 15px;
        }
        .error-message {
            color: #dc3545;
            margin-bottom: 1rem;
            padding: 0.5rem;
            border-radius: 4px;
            background-color: #f8d7da;
            border: 1px solid #f5c6cb;
        }
    </style>
</head>
<div class="container mt-3 d-flex justify-content-center text-center">
    <c:if test="${not empty provinces}">
        <div class="alert alert-info">
            Available Provinces:
            <c:forEach items="${provinces}" var="province">
                <div>ID: ${province.locationId} - Name: ${province.locationName}</div>
            </c:forEach>
        </div>
    </c:if>
    
    <c:if test="${empty provinces}">
        <div class="alert alert-warning">
            Provinces Registered in the system are displayed here.
        </div>
    </c:if>
</div>

<body>
    <div class="container">
        <div class="location-form">
            <h2 class="text-center mb-4">Location Management</h2>
            
            <c:if test="${not empty error}">
                <div class="error-message">
                    ${error}
                </div>
            </c:if>
            
            <form action="LocationServlet" method="POST" id="locationForm" onsubmit="return validateForm()">
                <input type="hidden" name="action" value="save">
                
                <div class="row">
                    <!-- Location Type Selection -->
                    <div class="col-md-6 mb-3">
                        <label for="locationType" class="form-label">Location Type*</label>
                        <select class="form-select" id="locationType" name="locationType" required onchange="handleLocationTypeChange()">
                            <option value="">Select Location Type</option>
                            <option value="PROVINCE" ${editLocation.locationType == 'PROVINCE' ? 'selected' : ''}>Province</option>
                            <option value="DISTRICT" ${editLocation.locationType == 'DISTRICT' ? 'selected' : ''}>District</option>
                            <option value="SECTOR" ${editLocation.locationType == 'SECTOR' ? 'selected' : ''}>Sector</option>
                            <option value="CELL" ${editLocation.locationType == 'CELL' ? 'selected' : ''}>Cell</option>
                            <option value="VILLAGE" ${editLocation.locationType == 'VILLAGE' ? 'selected' : ''}>Village</option>
                        </select>
                    </div>

                    <!-- Location Code -->
                    <div class="col-md-6 mb-3">
                        <label for="locationCode" class="form-label">Location Code*</label>
                        <input type="text" class="form-control" id="locationCode" name="locationCode" 
                               required value="${editLocation.locationCode}"
                               pattern="^(PRV|DST|SEC|CEL|VIL)[0-9]{3}$"
                               title="Format: PRV001, DST001, SEC001, CEL001, or VIL001">
                    </div>
                </div>

                <!-- Location Name -->
                <div class="mb-3">
                    <label for="locationName" class="form-label">Location Name*</label>
                    <input type="text" class="form-control" id="locationName" name="locationName" 
                           required value="${editLocation.locationName}"
                           pattern="^[A-Za-z\s-]{2,100}$"
                           title="2-100 characters, letters, spaces, and hyphens only">
                </div>

                <!-- Parent Location Selection -->
<div id="parentLocationSection">
    <!-- Province Selection -->
    <div class="hierarchy-select" id="provinceSelect" style="display: none;">
        <label for="provinceId" class="form-label">Province*</label>
        <select class="form-select" id="provinceId" name="provinceId" disabled>
            <option value="">Select Province</option>
            <c:forEach items="${provinces}" var="province">
                <option value="${province.locationId}">${province.locationName}</option>
            </c:forEach>
        </select>
    </div>

    <!-- District Selection -->
    <div class="hierarchy-select" id="districtSelect" style="display: none;">
        <label for="districtId" class="form-label">District*</label>
        <select class="form-select" id="districtId" name="districtId" disabled>
            <option value="">Select District</option>
        </select>
    </div>

    <!-- Sector Selection -->
    <div class="hierarchy-select" id="sectorSelect" style="display: none;">
        <label for="sectorId" class="form-label">Sector*</label>
        <select class="form-select" id="sectorId" name="sectorId" disabled>
            <option value="">Select Sector</option>
        </select>
    </div>

    <!-- Cell Selection -->
    <div class="hierarchy-select" id="cellSelect" style="display: none;">
        <label for="cellId" class="form-label">Cell*</label>
        <select class="form-select" id="cellId" name="cellId" disabled>
            <option value="">Select Cell</option>
        </select>
    </div>
</div>

                <div class="mt-4">
                    <button type="submit" class="btn btn-primary">Save Location</button>
                    <button type="reset" class="btn btn-secondary ms-2" onclick="resetForm()">Reset</button>
                    <a href="${pageContext.request.contextPath}/LocationServlet?action=getLocations" 
                       class="btn btn-light ms-2">Cancel</a>
                </div>
            </form>

            <!-- Location List Table -->
            <div class="mt-5">
                <h3>Location List</h3>
                <div class="table-responsive">
                    <table class="table table-striped table-hover">
                        <thead class="table-light">
                            <tr>
                                <th>Location Type</th>
                                <th>Code</th>
                                <th>Name</th>
                                <th>Parent Location</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${locations}" var="location">
                                <tr>
                                    <td>${location.locationType}</td>
                                    <td>${location.locationCode}</td>
                                    <td>${location.locationName}</td>
                                    <td>${location.parent.locationName}</td>
                                    <td>
                                        <div class="btn-group" role="group">
                                            <button class="btn btn-sm btn-warning" 
                                                    onclick="editLocation('${location.locationId}')">
                                                Edit
                                            </button>
                                            <button class="btn btn-sm btn-danger ms-1" 
                                                    onclick="deleteLocation('${location.locationId}')">
                                                Delete
                                            </button>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.0/js/bootstrap.bundle.min.js"></script>
    <script>
 // Utility function to show error messages
    function showError(message, container) {
        const errorDiv = document.createElement('div');
        errorDiv.className = 'alert alert-danger mt-2';
        errorDiv.textContent = message;
        
        const existingError = container.querySelector('.alert-danger');
        if (existingError) {
            existingError.remove();
        }
        
        container.appendChild(errorDiv);
        setTimeout(() => errorDiv.remove(), 5000);
    }
 
 // Add this function to debug location selection
    function debugLocationSelection(selectElement, locationType) {
        console.log(`\n=== Debug ${locationType} Selection ===`);
        console.log('Selected value:', selectElement.value);
        console.log('Selected option:', selectElement.options[selectElement.selectedIndex]?.text);
        
        // Log all available options
        console.log(`All ${locationType} options:`);
        Array.from(selectElement.options).forEach(option => {
            console.log(`- Value: ${option.value}, Text: ${option.text}`);
        });
    }

    // Updated function to handle province change
    function handleProvinceChange(provinceId) {
        const districtSelect = document.getElementById('districtId');
        
        console.log('Province selected:', provinceId);
        
        if (!provinceId) {
            clearDependentDropdowns('districtId');
            return;
        }
        
        // Clear existing options and show loading state
        districtSelect.disabled = true;
        districtSelect.innerHTML = '<option value="">Loading districts...</option>';
        
        // Remove any existing error messages
        const existingErrors = districtSelect.parentNode.querySelectorAll('.alert-danger');
        existingErrors.forEach(error => error.remove());
        
        // Get the context path and construct URL
        const contextPath = document.querySelector('meta[name="contextPath"]')?.getAttribute('content') || '';
        const url = contextPath + '/LocationServlet';
        const params = new URLSearchParams();
        params.append('action', 'getChildren');
        params.append('parentId', provinceId);
        
        // Make the AJAX request
        fetch(url + '?' + params.toString())
            .then(response => response.text())
            .then(text => {
                console.log('Raw server response:', text);
                try {
                    return JSON.parse(text);
                } catch (e) {
                    console.error('JSON parse error:', e);
                    throw new Error('Invalid server response format');
                }
            })
            .then(data => {
                console.log('Processed district data:', data);
                
                // Clear and initialize select
                districtSelect.innerHTML = '<option value="">Select District</option>';
                
                if (Array.isArray(data) && data.length > 0) {
                    // Add new options
                    data.forEach(district => {
                        const option = document.createElement('option');
                        option.value = district.locationId;
                        option.textContent = district.locationName + ' (' + district.locationCode + ')';
                        districtSelect.appendChild(option);
                    });
                    districtSelect.disabled = false;
                } else {
                    throw new Error('No districts found for this province');
                }
            })
            .catch(error => {
                console.error('Error loading districts:', error);
                
                // Show error message
                const errorDiv = document.createElement('div');
                errorDiv.className = 'alert alert-danger mt-2';
                errorDiv.textContent = 'Error loading districts: ' + error.message;
                districtSelect.parentNode.appendChild(errorDiv);
                
                // Reset select
                districtSelect.innerHTML = '<option value="">Select District</option>';
                districtSelect.disabled = true;
            });
    }

    // Function to load child locations (for other hierarchy levels)
    function loadChildLocations(parentId, targetSelectId, locationType) {
        if (!parentId) {
            console.error('No parent ID provided');
            clearDependentDropdowns(targetSelectId);
            return;
        }

        const select = document.getElementById(targetSelectId);
        if (!select) {
            console.error('Select element ' + targetSelectId + ' not found');
            return;
        }

        // Clear existing options and show loading state
        select.disabled = true;
        select.innerHTML = '<option value="">Loading...</option>';

        // Remove any existing error messages
        const existingErrors = select.parentNode.querySelectorAll('.alert-danger');
        existingErrors.forEach(error => error.remove());

        // Get the context path and construct URL
        const contextPath = document.querySelector('meta[name="contextPath"]')?.getAttribute('content') || '';
        const url = contextPath + '/LocationServlet';
        const params = new URLSearchParams();
        params.append('action', 'getChildren');
        params.append('parentId', parentId.toString().trim());
        
        console.log('Loading ' + locationType + 's for parent ID:', parentId);
        console.log('Request URL:', url + '?' + params.toString());

        fetch(url + '?' + params.toString())
            .then(response => response.text())
            .then(text => {
                console.log('Raw response:', text);
                try {
                    return JSON.parse(text);
                } catch (e) {
                    throw new Error('Invalid server response format');
                }
            })
            .then(data => {
                if (!Array.isArray(data)) {
                    throw new Error('Server returned invalid data format');
                }

                select.innerHTML = '<option value="">Select ' + locationType + '</option>';
                
                if (data.length > 0) {
                    data.forEach(location => {
                        const option = document.createElement('option');
                        option.value = location.locationId;
                        option.textContent = location.locationName + ' (' + location.locationCode + ')';
                        select.appendChild(option);
                    });
                    select.disabled = false;
                } else {
                    throw new Error('No ' + locationType + 's found');
                }
            })
            .catch(error => {
                console.error('Error loading locations:', error);
                select.innerHTML = '<option value="">Select ' + locationType + '</option>';
                select.disabled = true;
                
                const errorDiv = document.createElement('div');
                errorDiv.className = 'alert alert-danger mt-2';
                errorDiv.textContent = 'Error loading ' + locationType.toLowerCase() + 's: ' + error.message;
                select.parentNode.appendChild(errorDiv);
                
                setTimeout(() => errorDiv.remove(), 5000);
            });
    }

    // Initialize event listeners
    document.addEventListener('DOMContentLoaded', function() {
    const provinceSelect = document.getElementById('provinceId');
    const districtSelect = document.getElementById('districtId');
    const sectorSelect = document.getElementById('sectorId');
    const cellSelect = document.getElementById('cellId');

    if (provinceSelect) {
        provinceSelect.addEventListener('change', function() {
            debugLocationSelection(this, 'Province');
            handleProvinceChange(this.value);
        });
    }

    if (districtSelect) {
        districtSelect.addEventListener('change', function() {
            debugLocationSelection(this, 'District');
            const districtId = this.value;
            if (districtId) {
                loadChildLocations(districtId, 'sectorId', 'Sector');
            } else {
                clearDependentDropdowns('sectorId');
            }
        });
    }

    if (sectorSelect) {
        sectorSelect.addEventListener('change', function() {
            debugLocationSelection(this, 'Sector');
            const sectorId = this.value;
            if (sectorId) {
                loadChildLocations(sectorId, 'cellId', 'Cell');
            } else {
                clearDependentDropdowns('cellId');
            }
        });
    }

    if (cellSelect) {
        cellSelect.addEventListener('change', function() {
            debugLocationSelection(this, 'Cell');
        });
    }
});

// Rest of your existing functions remain the same
function handleLocationTypeChange() {
    const locationType = document.getElementById('locationType').value;
    console.log('Location type changed to:', locationType);
    
    const parentSections = {
        'DISTRICT': ['provinceSelect'],
        'SECTOR': ['provinceSelect', 'districtSelect'],
        'CELL': ['provinceSelect', 'districtSelect', 'sectorSelect'],
        'VILLAGE': ['provinceSelect', 'districtSelect', 'sectorSelect', 'cellSelect']
    };
    
    ['provinceSelect', 'districtSelect', 'sectorSelect', 'cellSelect'].forEach(section => {
        const element = document.getElementById(section);
        if (element) {
            element.style.display = 'none';
            const select = document.getElementById(section.replace('Select', 'Id'));
            if (select) {
                select.required = false;
                select.disabled = true;
            }
        }
    });

    if (parentSections[locationType]) {
        parentSections[locationType].forEach(section => {
            const element = document.getElementById(section);
            if (element) {
                element.style.display = 'block';
                const select = document.getElementById(section.replace('Select', 'Id'));
                if (select) {
                    select.required = true;
                    select.disabled = false;
                }
            }
        });
    }

    updateLocationCodePrefix(locationType);
}

function updateLocationCodePrefix(locationType) {
    const codeInput = document.getElementById('locationCode');
    if (!codeInput) return;

    const prefixMap = {
        'PROVINCE': 'PRV',
        'DISTRICT': 'DST',
        'SECTOR': 'SEC',
        'CELL': 'CEL',
        'VILLAGE': 'VIL'
    };
    
    if (prefixMap[locationType]) {
        const currentValue = codeInput.value;
        const newPrefix = prefixMap[locationType];
        if (!currentValue || !currentValue.startsWith(newPrefix)) {
            codeInput.value = newPrefix;
        }
    }
}

function clearDependentDropdowns(startFromId) {
    const dropdownOrder = ['districtId', 'sectorId', 'cellId'];
    let startClearing = false;
    
    dropdownOrder.forEach(id => {
        if (id === startFromId) {
            startClearing = true;
        }
        if (startClearing) {
            const select = document.getElementById(id);
            if (select) {
                select.innerHTML = `<option value="">Select location</option>`;
                select.disabled = true;
                
                const errorMessages = select.parentNode.querySelectorAll('.alert-danger');
                errorMessages.forEach(msg => msg.remove());
            }
        }
    });
}

function validateForm() {
    const locationType = document.getElementById('locationType').value;
    if (!locationType) {
        showError('Please select a location type', document.querySelector('.location-form'));
        return false;
    }

    const requiredFields = [
        { id: 'locationCode', label: 'Location Code' },
        { id: 'locationName', label: 'Location Name' }
    ];
    
    for (const field of requiredFields) {
        const element = document.getElementById(field.id);
        if (!element.value.trim()) {
            showError(`Please fill in ${field.label}`, document.querySelector('.location-form'));
            element.focus();
            return false;
        }
    }

    const parentRequirements = {
        'DISTRICT': [{ id: 'provinceId', label: 'Province' }],
        'SECTOR': [
            { id: 'provinceId', label: 'Province' },
            { id: 'districtId', label: 'District' }
        ],
        'CELL': [
            { id: 'provinceId', label: 'Province' },
            { id: 'districtId', label: 'District' },
            { id: 'sectorId', label: 'Sector' }
        ],
        'VILLAGE': [
            { id: 'provinceId', label: 'Province' },
            { id: 'districtId', label: 'District' },
            { id: 'sectorId', label: 'Sector' },
            { id: 'cellId', label: 'Cell' }
        ]
    };

    if (parentRequirements[locationType]) {
        for (const field of parentRequirements[locationType]) {
            const element = document.getElementById(field.id);
            if (!element.value) {
                showError(`Please select a ${field.label}`, document.querySelector('.location-form'));
                element.focus();
                return false;
            }
        }
    }

    return true;
}

function deleteLocation(locationId) {
    if (!confirm('Are you sure you want to delete this location? This action cannot be undone.')) {
        return;
    }

    const contextPath = document.querySelector('meta[name="contextPath"]')?.getAttribute('content') || '';
    const url = `${contextPath}/LocationServlet`;
    const params = new URLSearchParams({
        action: 'delete',
        locationId: locationId
    });

    fetch(`${url}?${params}`, {
        method: 'POST',
    })
    .then(response => {
        if (!response.ok) {
            return response.text().then(text => {
                throw new Error(text || 'Failed to delete location');
            });
        }
        window.location.reload();
    })
    .catch(error => {
        console.error('Error deleting location:', error);
        showError('Error deleting location: ' + error.message, document.querySelector('.location-form'));
    });
}

function editLocation(locationId) {
    window.location.href = `${window.location.pathname}?action=edit&id=${locationId}`;
}

function resetForm() {
    document.getElementById('locationForm').reset();
    handleLocationTypeChange();
    const errorMessages = document.querySelectorAll('.alert-danger');
    errorMessages.forEach(msg => msg.remove());
}

// Debug utility function
function debugFormData() {
    console.log('Current form state:');
    const formElements = {
        'locationType': 'Location Type',
        'locationCode': 'Location Code',
        'locationName': 'Location Name',
        'provinceId': 'Province',
        'districtId': 'District',
        'sectorId': 'Sector',
        'cellId': 'Cell'
    };

    for (const [id, label] of Object.entries(formElements)) {
        const element = document.getElementById(id);
        console.log(`${label}:`, element ? element.value : 'Element not found');
    }
}
</script>
</body>
</html>