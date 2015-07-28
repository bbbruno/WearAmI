##################
# Reserved words #
#################################################################
#                                                               #
#   Head                                                        #
#   Resource                                                    #
#   Sensor                                                      #
#   ContextVariable                                             #
#   SimpleOperator                                              #
#   SimpleDomain                                                #
#   Constraint                                                  #
#   RequiredState						                        #
#   AchievedState						                        #
#   RequiredResource						                    #
#   All AllenIntervalConstraint types                           #
#   '[' and ']' should be used only for constraint bounds       #
#   '(' and ')' are used for parsing                            #
#                                                               #
#################################################################

####################
# Domain: testPEIS #
#################################################
# Sensors:                                      #
# 01. Chair (pressure sensor)                   #
# 02. Armchair (pressure sensor)                #
#                                               #
# Recognized Activities:                        #
# 1. SitDown (chair)                            #
# 2. SitDown (armchair)                         #
#################################################


(Domain TestPEIS)

(Sensor Chair)
(Sensor Armchair)

(ContextVariable Human)

###################
# SitDown (chair) #
###################
(SimpleOperator
 (Head Human::SitDownChair())
 (RequiredState req1 Chair::On())
 (Constraint Equals(Head,req1))
)

######################
# SitDown (armchair) #
######################
(SimpleOperator
 (Head Human::SitDownArmchair())
 (RequiredState req1 Armchair::On())
 (Constraint Equals(Head,req1))
)