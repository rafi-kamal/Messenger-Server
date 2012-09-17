-- phpMyAdmin SQL Dump
-- version 3.5.1
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Aug 24, 2012 at 11:19 AM
-- Server version: 5.5.24-log
-- PHP Version: 5.3.13

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `client`
--

-- --------------------------------------------------------

--
-- Table structure for table `client_friendship`
--

CREATE TABLE IF NOT EXISTS `client_friendship` (
  `Client_id` int(11) NOT NULL,
  `Friend_id` int(11) NOT NULL,
  PRIMARY KEY (`Client_id`,`Friend_id`),
  KEY `Client_id` (`Client_id`),
  KEY `Friend_id` (`Friend_id`),
  KEY `Friend_id_2` (`Friend_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `client_friendship`
--

INSERT INTO `client_friendship` (`Client_id`, `Friend_id`) VALUES
(1, 2),
(1, 3),
(2, 4),
(3, 1),
(3, 2),
(4, 5),
(5, 2),
(5, 4);

-- --------------------------------------------------------

--
-- Table structure for table `client_info`
--

CREATE TABLE IF NOT EXISTS `client_info` (
  `ID` int(11) NOT NULL,
  `Full Name` varchar(30) DEFAULT NULL,
  `Address` varchar(40) DEFAULT NULL,
  `Date of Birth` date DEFAULT NULL,
  `Institution` varchar(30) DEFAULT NULL,
  `Quote` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `ID` (`ID`),
  KEY `ID_2` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `client_info`
--

INSERT INTO `client_info` (`ID`, `Full Name`, `Address`, `Date of Birth`, `Institution`, `Quote`) VALUES
(1, 'Rafi Kamal', 'Ahsan Ullah Hall', '1993-06-02', 'BUET', 'I love programming'),
(2, 'Jamshed Khan Mukut', 'Mirpur', '1991-05-01', 'BUET', 'I also love programming :)'),
(3, 'Moinul Shaon', 'Mirpur', '1992-06-05', 'BUET', NULL),
(4, 'Tanzima Noor Tazin', 'Khagrachori', '1994-11-02', 'Chakaria Girls College', 'I hate study :('),
(5, 'Ahamed Al Nahian', 'Mohammadpur', '1990-02-21', 'BUET', 'I love number theory');

-- --------------------------------------------------------

--
-- Table structure for table `client_logger`
--

CREATE TABLE IF NOT EXISTS `client_logger` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Username` varchar(30) NOT NULL,
  `Password` varchar(30) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `Username` (`Username`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=16 ;

--
-- Dumping data for table `client_logger`
--

INSERT INTO `client_logger` (`ID`, `Username`, `Password`) VALUES
(1, 'Rafi', '1234'),
(2, 'Mukut', 'abcd'),
(3, 'Shaon', 'abc'),
(4, 'Tazin', 'xyz'),
(5, 'Nahian', '0070'),
(9, 'Faysal', 'faysal'),
(10, 'Shafin', '3210'),
(11, 'Tamal', 'stamal'),
(12, 'Jami', '7894'),
(14, 'Shamim', '4561'),
(15, 'Sami', 'qwerty');

-- --------------------------------------------------------

--
-- Table structure for table `online_clients`
--

CREATE TABLE IF NOT EXISTS `online_clients` (
  `ID` int(11) NOT NULL,
  KEY `ID` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `online_clients`
--

INSERT INTO `online_clients` (`ID`) VALUES
(1),
(3),
(4);

--
-- Constraints for dumped tables
--

--
-- Constraints for table `client_friendship`
--
ALTER TABLE `client_friendship`
  ADD CONSTRAINT `client_friendship_ibfk_3` FOREIGN KEY (`Client_id`) REFERENCES `client_logger` (`ID`),
  ADD CONSTRAINT `client_friendship_ibfk_4` FOREIGN KEY (`Friend_id`) REFERENCES `client_logger` (`ID`);

--
-- Constraints for table `client_info`
--
ALTER TABLE `client_info`
  ADD CONSTRAINT `client_info_ibfk_1` FOREIGN KEY (`ID`) REFERENCES `client_logger` (`ID`);

--
-- Constraints for table `online_clients`
--
ALTER TABLE `online_clients`
  ADD CONSTRAINT `online_clients_ibfk_2` FOREIGN KEY (`ID`) REFERENCES `client_logger` (`ID`);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
