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

#################################################
# Domain: WearAmI@Angen (RO-MAN scenarios only) #
#################################################
# Sensors:                                      #
# 01. Location (PIR)                            #
# 02. Gesture (smartwatch)                      #
# 03. Posture (waist-placed fall detector)      #
# 04. Chair (pressure sensor)                   #
# 05. Armchair (pressure sensor)                #
# 10. Glass (proximity sensor under the table)  #
# 11. Bottle (proximity sensor under the table) #
#                                               #
# Recognized Activities:                        #
# 1. SitDown (chair)                            #
# 1. SitDown (armchair)                         #
# 2. StandUp                                    #
# 7. Drink::Pour                                #
# 5. Eat (Drink::Pour)                          #
# 8. Drink::OneGulp                             #
# 5. Eat (Drink::OneGulp)                       #
# 10. ClimbStairs                               #
# 11. DescendStairs                             #
# 12. Walk                                      #
#################################################


(Domain WearamiAngenROMAN)

(Sensor Location)
(Sensor Gesture)
(Sensor Posture)
(Sensor Chair)
(Sensor Armchair)
(Sensor Glass)
(Sensor Bottle)

(ContextVariable Human)
(ContextVariable Drink)

###################
# SitDown (chair) #
###################
(SimpleOperator
 (Head Human::SitDown())
 (RequiredState req1 Gesture::Sit())
 (RequiredState req2 Posture::Sitting())
 (RequiredState req3 Chair::On())
 (Constraint OverlappedBy(Head,req1))
 (Constraint During(Head,req2))
 (Constraint EndEnd(Head,req3))
)

######################
# SitDown (armchair) #
######################
(SimpleOperator
 (Head Human::SitDown())
 (RequiredState req1 Gesture::Sit())
 (RequiredState req2 Posture::Sitting())
 (RequiredState req3 Armchair::On())
 (Constraint OverlappedBy(Head,req1))
 (Constraint During(Head,req2))
 (Constraint EndEnd(Head,req3))
)

#####################
# StandUp (generic) #
#####################
(SimpleOperator
 (Head Human::StandUp())
 (RequiredState req1 Gesture::Stand())
 (RequiredState req2 Posture::Standing())
 (RequiredState req3 Human::SitDown())
 (Constraint MetByOrOverlappedBy(Head,req1))
 (Constraint Starts(Head,req2))
 (Constraint MetByOrAfter(Head,req3))
)

###############################
# Drink::Pour (PourS & PourE) #
###############################
(SimpleOperator
 (Head Drink::Pour())
 (RequiredState req1 Location::Kitchen())
 (RequiredState req2 Gesture::PourS())
 (RequiredState req3 Gesture::PourE())
 (RequiredState req4 Glass::On())
 (RequiredState req5 Bottle::On())
 (Constraint During(Head,req1))
 (Constraint StartedBy(Head,req2))
 (Constraint FinishedBy(Head,req3))
 (Constraint During(Head,req4))
 (Constraint During(Head,req5))
)

################################
# Eat (Drink::Pour) - contains #
################################
(SimpleOperator
 (Head Human::Eat())
 (RequiredState req1 Drink::Pour())
 (Constraint Contains(Head,req1))
)

###########################################
# Drink::OneGulp (PickUp & PutDown glass) #
###########################################
(SimpleOperator
 (Head Drink::OneGulp())
 (RequiredState req1 Location::Kitchen())
 (RequiredState req2 Gesture::PickUp())
 (RequiredState req3 Gesture::PutDown())
 (RequiredState req4 Glass::On())
 (Constraint During(Head,req1))
 (Constraint StartedBy(Head,req2))
 (Constraint FinishedBy(Head,req3))
 (Constraint During(Head,req4))
)

###################################
# Eat (Drink::OneGulp) - contains #
###################################
(SimpleOperator
 (Head Human::Eat())
 (RequiredState req1 Drink::OneGulp())
 (Constraint Contains(Head,req1))
)

################
# Climb stairs #
################
(SimpleOperator
 (Head Human::ClimbStairs())
 (RequiredState req1 Posture::Standing())
 (RequiredState req2 Gesture::Walk())
 (RequiredState req3 Location::Staircase())
 (RequiredState req4 Location::EntranceHall())
 (Constraint During(Head,req1))
 (Constraint During(Head,req2))
 (Constraint Equals(Head,req3))
 (Constraint MetByOrAfter(Head,req4))
)

##################
# Descend stairs #
##################
(SimpleOperator
 (Head Human::DescendStairs())
 (RequiredState req1 Posture::Standing())
 (RequiredState req2 Gesture::Walk())
 (RequiredState req3 Location::Staircase())
 (RequiredState req4 Location::LaundryRoom())
 (Constraint During(Head,req1))
 (Constraint During(Head,req2))
 (Constraint Equals(Head,req3))
 (Constraint MetByOrAfter(Head,req4))
)

########
# Walk #
########
(SimpleOperator
 (Head Human::Walk())
 (RequiredState req1 Posture::Standing())
 (RequiredState req2 Gesture::Walk())
 (Constraint During(Head,req1))
 (Constraint Equals(Head,req2))
)
