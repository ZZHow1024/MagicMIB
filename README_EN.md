<div align="center">
  <a href="https://github.com/ZZHow1024/MagicMIB">
    <img src="backend/src/main/resources/image/MagicMIB.png" width="20%" alt="MagicMIB" />
  </a>
  <h1>MagicMIB</h1>
</div>
<div align="center" style="line-height: 1;">
  <a href="https://github.com/ZZHow1024/MagicMIB/releases"><img alt="MagicMIB-v1.0.0"
    src="https://img.shields.io/badge/MagicMIB-v1.0.0-blue"/>
  </a>
  <a href="LICENSE"><img alt="Code License"
    src="https://img.shields.io/github/license/ZZHow1024/MagicMIB">
  </a>
</div>

# **MagicMIB (English)**

[**中文说明**](./README.md)

---

Website:

[MagicMIB (English) | ZZHow](https://www.zzhow.com/MagicMIBEN)

Source Code:

https://github.com/ZZHow1024/MagicMIB

Releases:

[**https://github.com/ZZHow1024/MagicMIB/releases**](https://github.com/ZZHow1024/MagicMIB/releases)

---

## What is it?

**MagicMIB** is a network device management tool based on the SNMP protocol. It provides both graphical and web interfaces, supporting MIB browsing and SNMP operations such as Get/Set/GetNext/GetBulk/Walk/GetSubtree, helping network administrators easily manage and monitor network devices.

---

## Technical Route

![TechnicalRoute.png](./TechnicalRoute.png)

- **Backend Framework**: Spring Boot 3.5.8
- **Frontend Framework**: Vue 3 + Vite
- **UI Component Library**: Arco Design Vue
- **SNMP Protocol**: SNMP4J 3.9.6
- **MIB Parsing**: Mibble 2.10.1
- **GUI**: JavaFX 21
- **Programming Languages**: Java 21, JavaScript/TypeScript
- **Build Tools**: Maven (backend), pnpm (frontend)

---

## **License**

This project is licensed under the GNU General Public License v3.0 - see the [LICENSE](./LICENSE) file for details.

---

## Features

### Core Features

- **MIB Browser**: Visual MIB tree structure, supports MIB file loading and management
- **SNMP Operations**:
  - **Get**: Retrieve value of specified OID
  - **GetNext**: Retrieve next OID value
  - **GetBulk**: Batch retrieve multiple OID values
  - **Walk**: Iterate through all data following the specified OID
  - **GetSubtree**: Retrieve all data of specified OID subtree
  - **Set**: Modify device configuration parameters
- **Multi-protocol Support**: SNMP v1/v2c (v3 in development)
- **Authentication**: Support for read/write community name configuration

### Interface Features

- **Dual Interface Mode**:
  - **Desktop Application**: JavaFX-based graphical startup interface
  - **Web Application**: Vue 3-based responsive web interface
- **Real-time Result Display**: Tabular display of query results
- **Object Details**: Display detailed MIB node information (name, OID, syntax, access, status, description)
- **Convenient Operations**: One-click operation buttons, simplified workflow

---

## System Requirements

### Backend Runtime Environment

- **Java**: JDK 21 or higher
- **Maven**: 3.6 or higher
- **Operating System**: Windows 10+, macOS 11+, Linux

### Frontend Runtime Environment

- **Node.js**: 20.19.0 or 22.12.0+
- **pnpm**: Recommended as package manager

### Browser Requirements

- Modern browsers (Chrome, Firefox, Edge, Safari) latest versions
- Support for modern JavaScript features

---

## Quick Start

### Method 1: Using Compiled Program

1. Visit [Releases](https://github.com/ZZHow1024/MagicMIB/releases) page to download the latest version
2. Select the appropriate installation package for your operating system
3. Run the program:
   - **Windows**: Double-click `.exe`/`.msi` installer, extract `.zip` archive and run, or run `.jar` file
   - **macOS**: Install `.dmg`/`.pkg` or run `.jar` file
   - **Linux**: Install `.deb`/`.rpm` package or run `.jar` file

### Method 2: Build from Source

#### Environment Preparation

1. Install JDK 21 or higher
2. Install Maven 3.6+
3. Install Node.js 20.19.0+ and pnpm

#### Clone Project

```bash
git clone https://github.com/ZZHow1024/MagicMIB.git
cd MagicMIB
```

#### Start Backend Service

```bash
cd backend
mvn clean compile
mvn spring-boot:run
```

#### Start Frontend Service (Development Mode)

```bash
cd frontend
pnpm install
pnpm dev
```

#### Access Application

- **Desktop Application**: Run the main class `MagicMibApplication` of the `backend` module
- **Web Application**: Access `http://localhost:5173` in browser

---

## Instructions for Use

### Desktop Application Workflow

1. **Launch Application**: Main window will display after running the program
2. **Configure Service**:
   - Set port number (default 80)
   - Choose whether to allow LAN access
3. **Start Service**: Click "Start Service" button
4. **Access Web Interface**: Open the displayed URL in browser
5. **Configure SNMP Parameters**:
   - Set target device IP address
   - Configure port (default 161)
   - Set read/write community names (default public)
   - Select SNMP version (v1/v2c)
6. **Execute Operation**:
   - Browse and select nodes in MIB tree
   - Or enter OID directly
   - Select operation type (Get/GetNext/GetBulk/Walk/GetSubtree/Set)
   - Click "Go" button to execute
7. **View Results**: Results will be displayed in the result table on the right

### Web Application Workflow

1. **Start Backend Service** (Ensure Spring Boot service is running)
2. **Start Frontend Development Server** or access deployed Web application
3. **Configure Authentication**: Click "Advanced..." button to set SNMP parameters
4. **Browse MIB Tree**: Left MIB tree displays available MIB modules and objects
5. **Select Operation**:
   - Click tree nodes to automatically fill OID
   - Select operation type
   - Click "Go" to execute
6. **Manage MIB Tree**: Click "Manage MIB Tree" button to load/unload MIB files
7. **View Results**: Right table displays query results, supports clearing and viewing multiple records

---

## Supported SNMP Operations Explained

### Get
Retrieve single value of specified OID. Suitable for querying specific device information.

### GetNext
Retrieve next object value of specified OID. Used for traversing MIB tree.

### GetBulk
Batch retrieve multiple OID values. Efficient for retrieving large amounts of data.

### Walk
Iterate through all data following the specified OID.

### GetSubtree
Retrieve all data of specified OID subtree.

### Set
Modify device configuration parameters. Requires community name with write permission.

---

## Project Structure

```
MagicMIB/
├── backend/                    # Backend module
│   ├── src/main/java/
│   │   └── com/zzhow/magicmibbackend/
│   │       ├── controller/     # REST API controllers
│   │       ├── service/        # Business logic services
│   │       ├── repository/     # Data access layer
│   │       ├── pojo/           # Data objects
│   │       ├── ui/             # JavaFX interface
│   │       ├── config/         # Configuration classes
│   │       ├── util/           # Utility classes
│   │       └── MagicMibApplication.java  # Application entry
│   └── pom.xml                 # Maven configuration
├── frontend/                   # Frontend module
│   ├── src/
│   │   ├── api/                # API interfaces
│   │   ├── components/         # Vue components
│   │   ├── views/              # Page views
│   │   ├── layout/             # Layout components
│   │   ├── stores/             # Pinia state management
│   │   └── router/             # Router configuration
│   └── package.json            # Node.js dependencies
├── README.md                   # Chinese documentation
└── README_EN.md                # English documentation
```

---

## Development Guide

### Backend Development

```bash
# Enter backend directory
cd backend

# Compile project
mvn clean compile

# Run tests
mvn test

# Package
mvn clean package

# Run application
mvn spring-boot:run
```

### Frontend Development

```bash
# Enter frontend directory
cd frontend

# Install dependencies
pnpm install

# Start development server
pnpm dev

# Lint and fix code
pnpm lint

# Format code
pnpm format

# Build production version
pnpm build
```

---

## FAQ

### Q: Cannot connect to SNMP device
A: Please check:
- Is the target device IP address correct?
- Is the port correct (default 161)?
- Is the community name correct?
- Does the target device allow SNMP access?
- Is the network connection normal?

### Q: MIB tree is empty or fails to load
A: Please check:
- Are MIB files loaded correctly?
- Is the MIB file format correct?
- Have you selected the correct MIB module?

### Q: Cannot access Web interface
A: Please check:
- Is the backend service started?
- Is the port occupied?
- Is the firewall blocking access?
- Does the browser support modern JavaScript features?

---

## Contribution Guidelines

Welcome to submit Issues and Pull Requests to improve project features!

### Contribution Method

1. Fork the project
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Open Pull Request

---

## Version History

### v1.0.0 (Current Version)
- Initial version release
- Implemented basic MIB browser functionality
- Implemented basic SNMP terminal functionality

---

## Contact

- **Author**: ZZHow(ZZHow1024)
- **GitHub**: https://github.com/ZZHow1024/MagicMIB
- **Project Website**: https://www.zzhow.com/MagicMIBEN

---

## Acknowledgements

- [Spring Boot](https://spring.io/projects/spring-boot) - Backend framework
- [Vue 3](https://vuejs.org/) - Frontend framework
- [Arco Design Vue](https://arco.design/vue) - UI component library
- [SNMP4J](http://www.snmp4j.org/) - SNMP protocol library
- [Mibble](https://www.mibble.org/) - MIB parsing library
- [JavaFX](https://openjfx.io/) - Desktop GUI