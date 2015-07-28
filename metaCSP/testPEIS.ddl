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
# 01. Location (PIR)                            #
#                                               #
# Recognized Activities:                        #
# (debug mode)                                  #
# a. InLivingroom                               #
# b. InKitchen                                  #
#################################################


(Domain TestPEIS)

(Sensor Location)

(ContextVariable Human)

################
# InLivingroom #
################
(SimpleOperator
 (Head Human::InLivingroom())
 (RequiredState req1 Location::Livingroom())
 (Constraint Equals(Head,req1))
)

#############
# InKitchen #
#############
(SimpleOperator
 (Head Human::InKitchen())
 (RequiredState req1 Location::Kitchen())
 (Constraint Equals(Head,req1))
)
